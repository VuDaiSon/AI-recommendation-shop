package com.example.recommendershop.service.featuredPost;

import com.example.recommendershop.dto.ResponseData;
import com.example.recommendershop.dto.featuredPost.request.FeaturedPostRequest;
import com.example.recommendershop.dto.featuredPost.response.FeaturedPostResponse;

import java.util.List;
import java.util.UUID;

public interface FeaturedPostService {
    FeaturedPostResponse create(FeaturedPostRequest featuredPostRequest);
    FeaturedPostResponse update(UUID featuredPostId, FeaturedPostRequest featuredPostRequest);
    ResponseData<?> delete(UUID featuredPostId);
    List<FeaturedPostResponse> getAll();
}
