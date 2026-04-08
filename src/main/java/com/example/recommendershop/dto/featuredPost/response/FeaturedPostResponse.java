package com.example.recommendershop.dto.featuredPost.response;


import com.example.recommendershop.dto.category.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedPostResponse {
    private UUID featuredPostId;
    private String url;
    private CategoryResponse category;

}
