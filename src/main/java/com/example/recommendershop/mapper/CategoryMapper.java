package com.example.recommendershop.mapper;

import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.category.request.CategoryRequest;
import com.example.recommendershop.dto.category.response.CategoryAvatar;
import com.example.recommendershop.dto.category.response.CategoryResponse;
import com.example.recommendershop.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
        Category toEntity(CategoryRequest categoryRequest);
        CategoryResponse toDao(Category category);
        List<CategoryAvatar> toListDao(List<Category> categories);
        CategoryAvatar toAvatar(Category category, Long totalProducts);
        void update(CategoryRequest categoryRequest, @MappingTarget Category category);


}
