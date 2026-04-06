package com.example.recommendershop.service.product;
import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.SearchEntity;
import com.example.recommendershop.dto.product.request.ProductRequest;
import com.example.recommendershop.dto.product.response.ProductAvatar;
import com.example.recommendershop.dto.product.response.ProductResponse;
import com.example.recommendershop.entity.*;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.ProductMapper;
import com.example.recommendershop.repository.*;
import com.example.recommendershop.service.file.FileService;
import com.example.recommendershop.utils.FilterDataUtil;
import com.example.recommendershop.validation.Validator;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final PermissionCheck permissionCheck;
    private final Validator validator;
    private final FileService fileService;
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, PermissionCheck permissionCheck, Validator validator, FileService fileService){
        this.productRepository =productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.permissionCheck = permissionCheck;
        this.validator = validator;
        this.fileService = fileService;
    }
    @Override
    @Transactional

    public ProductResponse create(ProductRequest productRequest) {
        permissionCheck.checkPermission("add");
        validator.checkEntityExists(productRepository.findProductByName(productRequest.getName()), HttpStatus.BAD_REQUEST, "Sản phẩm đã tồn tại");
        Category category = validator.checkEntityNotExists(categoryRepository.findById(productRequest.getCategoryId()), HttpStatus.NOT_FOUND, "Danh mục không tồn tại");
        Product product = productMapper.toEntity(productRequest);
        product.setCategory(category);
        product = productRepository.save(product);
        return productMapper.toDao(product);
    }
    @Override
    @Transactional
    public ProductResponse update(UUID productId, ProductRequest productRequest) {
        permissionCheck.checkPermission("update");
        Product product = validator.checkEntityNotExists(productRepository.findById(productId), HttpStatus.NOT_FOUND, "Sảm phẩm không tồn tại");
        Category category = validator.checkEntityNotExists(categoryRepository.findById(productRequest.getCategoryId()), HttpStatus.NOT_FOUND, "Danh mục không tồn tại");
        String oldImage = product.getImage();
        productMapper.update(productRequest, product);
        product.setCategory(category);
        Product updatedProduct = productRepository.save(product);
        if (oldImage != null && !oldImage.equals(updatedProduct.getImage())) {
            fileService.deleteFile(oldImage);
        }
         return productMapper.toDao(updatedProduct);
    }
    @Override
    public void delete(UUID productId) {
        permissionCheck.checkPermission("delete");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // 🔥 XÓA FILE ẢNH TRONG Ổ CỨNG
        fileService.deleteFileByUrl(product.getImage());
        // 🔥 XÓA DB
        productRepository.deleteById(productId);
    }
    public ProductResponse detail(UUID productId){
        if(!productRepository.existsById(productId)){
            throw new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        return productMapper.toDao(productRepository.getReferenceById(productId));
    }
    protected BasePage<ProductAvatar> map(Page<Product> page) {
        BasePage<ProductAvatar> rPage = new BasePage<>();
        rPage.setData(productMapper.toListDao(page.getContent()));
        rPage.setTotalPage(page.getTotalPages());
        rPage.setTotalRecord( page.getTotalElements());
        rPage.setPage(page.getPageable().getPageNumber());
        return rPage;
    }

    public BasePage<ProductAvatar> getAll(ApiListBaseRequest listRequest){
        Page<Product> page = productRepository.findAll(FilterDataUtil.buildPageRequest(listRequest));
        return this.map(page);
    }
    @Override
    public BasePage<ProductAvatar> search(SearchEntity search, ApiListBaseRequest filter) {
        String keyword = search.getKeyword();
        Specification<Product> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(keyword)) {
                predicates.add(builder.like(builder.upper(root.get("name")), "%" + keyword.trim().toUpperCase() + "%"));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> page = productRepository.findAll(spec, FilterDataUtil.buildPageRequest(filter));
        return this.map(page);
    }
    public BasePage<ProductAvatar> getProductsByCategory(UUID categoryId, ApiListBaseRequest listBaseRequest){
        Category category = validator.checkEntityNotExists(categoryRepository.findById(categoryId),HttpStatus.NOT_FOUND, "Danh mục rỗng");
        Page<Product> productPage = productRepository.findProductByCategory(category, FilterDataUtil.buildPageRequest(listBaseRequest));
        return this.map(productPage);
    }
}
