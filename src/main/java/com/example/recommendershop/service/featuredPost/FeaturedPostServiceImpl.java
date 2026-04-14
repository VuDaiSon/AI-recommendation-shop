package com.example.recommendershop.service.featuredPost;

import com.example.recommendershop.authorization.PermissionCheck;
import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.featuredPost.request.FeaturedPostRequest;
import com.example.recommendershop.dto.featuredPost.response.FeaturedPostResponse;
import com.example.recommendershop.entity.Category;
import com.example.recommendershop.entity.FeaturedPost;
import com.example.recommendershop.exception.MasterException;
import com.example.recommendershop.mapper.FeaturedPostMapper;
import com.example.recommendershop.repository.CategoryRepository;
import com.example.recommendershop.repository.FeaturedPostRepository;
import com.example.recommendershop.service.file.FileService;
import com.example.recommendershop.validation.ImageValidator;
import com.example.recommendershop.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeaturedPostServiceImpl implements FeaturedPostService{
private final FeaturedPostRepository featuredPostRepository;
private final PermissionCheck permissionCheck;
private final Validator validator;
private final CategoryRepository categoryRepository;
private final FeaturedPostMapper featuredPostMapper;
private final FileService fileService;
private final ImageValidator imageValidator;

    @Override
    public FeaturedPostResponse create(FeaturedPostRequest featuredPostRequest ) {
    permissionCheck.checkPermission("add");
        imageValidator.validateMainImage(featuredPostRequest.getUrl());
        imageValidator.validateFolder(featuredPostRequest.getUrl(), "banners");
        validator.checkEntityExists(featuredPostRepository.findByUrl(featuredPostRequest.getUrl()), HttpStatus.BAD_REQUEST, "Banner đã tồn tại");
    Category category = validator.checkEntityNotExists(categoryRepository.findById(featuredPostRequest.getCategoryId()), HttpStatus.NOT_FOUND, "Danh mục không tồn tại");
        FeaturedPost featuredPost = featuredPostMapper.toEntity(featuredPostRequest);
        featuredPost.setCategory(category);
        featuredPost = featuredPostRepository.save(featuredPost);
        return featuredPostMapper.toDao(featuredPost);
    }

    @Override
    public FeaturedPostResponse update(UUID featuredPostId, FeaturedPostRequest featuredPostRequest) {
        permissionCheck.checkPermission("update");
        FeaturedPost featuredPost = validator.checkEntityNotExists(featuredPostRepository.findById(featuredPostId), HttpStatus.NOT_FOUND, "Sảm phẩm không tồn tại");
        Category category = validator.checkEntityNotExists(categoryRepository.findById(featuredPostRequest.getCategoryId()), HttpStatus.NOT_FOUND, "Danh mục không tồn tại");
        String oldImage = featuredPost.getUrl();
        imageValidator.validateMainImage(featuredPostRequest.getUrl());
        imageValidator.validateFolder(featuredPostRequest.getUrl(), "banners");
        featuredPostMapper.update(featuredPostRequest, featuredPost);
        featuredPost.setCategory(category);
        FeaturedPost updatedfeaturedPost = featuredPostRepository.save(featuredPost);
        if (oldImage != null && !oldImage.equals(updatedfeaturedPost.getUrl())) {
            fileService.delete(oldImage);
        }

        return featuredPostMapper.toDao(updatedfeaturedPost);
    }

    @Override
    @Transactional
    public ResponseData<?> delete(UUID featuredPostId) {
        permissionCheck.checkPermission("delete");
        FeaturedPost featuredPost = featuredPostRepository.findById(featuredPostId)
                .orElseThrow(() -> new MasterException(HttpStatus.NOT_FOUND, "Không tìm thấy banner"));

        fileService.delete(featuredPost.getUrl());
        Category category = featuredPost.getCategory();
        if (category != null) {
            category.setFeatured(null);
        }
        featuredPostRepository.delete(featuredPost);
        return new ResponseData<>(HttpStatus.OK.value(), "Đã xóa thành công");
    }

    public List<FeaturedPostResponse> getAll() {
        List<FeaturedPost> featuredPosts = featuredPostRepository.findAll();
        return featuredPosts.stream()
                .map(featuredPostMapper::toDao)
                .toList();
    }
}
