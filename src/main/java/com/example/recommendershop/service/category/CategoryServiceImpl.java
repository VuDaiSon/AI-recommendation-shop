package com.example.recommendershop.service.category;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.category.request.CategoryRequest;
import com.example.recommendershop.dto.category.response.CategoryAvatar;
import com.example.recommendershop.dto.category.response.CategoryResponse;
import com.example.recommendershop.entity.*;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.CategoryMapper;
import com.example.recommendershop.repository.*;
import com.example.recommendershop.service.file.FileService;
import com.example.recommendershop.utils.FilterDataUtil;
import com.example.recommendershop.validation.Validator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PermissionCheck permissionCheck;
    private final Validator validator;
    private final FileService fileService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, PermissionCheck permissionCheck, Validator validator, FileService fileService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.permissionCheck = permissionCheck;
        this.validator = validator;
        this.fileService = fileService;
    }

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        permissionCheck.checkPermission("add");
        validator.checkEntityExists(categoryRepository.findCategoryByName(categoryRequest.getName()), HttpStatus.BAD_REQUEST, "Danh mục đã tồn tại");
        Category category = categoryRepository.save(categoryMapper.toEntity(categoryRequest));
        CategoryResponse categoryResponse = categoryMapper.toDao(category);
        System.out.print("Tạo sản phẩm thành công");
        return categoryResponse;
    }

    @Override
    public CategoryResponse update(UUID categoryId, CategoryRequest categoryRequest) {
        permissionCheck.checkPermission("update");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"));

        // 🔥 lấy ảnh cũ
        String oldImage = category.getImage();

        // update dữ liệu
        categoryMapper.update(categoryRequest, category);

        Category saved = categoryRepository.save(category);

        // 🔥 nếu ảnh thay đổi → xóa ảnh cũ
        if (oldImage != null && !oldImage.equals(saved.getImage())) {
            fileService.deleteFile(oldImage);
        }

        return categoryMapper.toDao(saved);
    }

    @Override
    public CategoryResponse detail(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        return categoryMapper.toDao(categoryRepository.getReferenceById(categoryId));
    }

    @Override
    public void delete(UUID categoryId) {
        permissionCheck.checkPermission("delete");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // 🔥 XÓA FILE ẢNH TRONG Ổ CỨNG
        fileService.deleteFileByUrl(category.getImage());

        categoryRepository.deleteById(categoryId);
    }

    protected BasePage<CategoryAvatar> map(Page<Category> page) {

        BasePage<CategoryAvatar> rPage = new BasePage<>();
        rPage.setData(categoryMapper.toListDao(page.getContent()));
        rPage.setTotalPage(page.getTotalPages());
        rPage.setTotalRecord(page.getTotalElements());
        rPage.setPage(page.getPageable().getPageNumber());
        return rPage;
    }

    public BasePage<CategoryAvatar> getAll(ApiListBaseRequest listRequest) {
        Page<Category> page = categoryRepository.findAll(FilterDataUtil.buildPageRequest(listRequest));
        return this.map(page);
    }

}
