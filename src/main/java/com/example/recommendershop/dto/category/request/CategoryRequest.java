package com.example.recommendershop.dto.category.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotNull
    private String name;
    @NotNull
    private String image;
}
