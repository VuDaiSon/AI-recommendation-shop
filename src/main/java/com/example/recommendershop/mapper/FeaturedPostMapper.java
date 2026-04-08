package com.example.recommendershop.mapper;


import com.example.recommendershop.dto.featuredPost.request.FeaturedPostRequest;
import com.example.recommendershop.dto.featuredPost.response.FeaturedPostResponse;
import com.example.recommendershop.entity.FeaturedPost;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeaturedPostMapper {
    FeaturedPostResponse toDao(FeaturedPost entity);
    FeaturedPost toEntity(FeaturedPostRequest featuredPostRequest);
    List<FeaturedPostResponse> toListDao(List<FeaturedPost> featuredPosts);
    void update(FeaturedPostRequest featuredPostRequest, @MappingTarget FeaturedPost featuredPost);

}
