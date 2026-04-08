package com.example.recommendershop.dto.featuredPost.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedPostRequest {
    private String url;
    private UUID categoryId;


}
