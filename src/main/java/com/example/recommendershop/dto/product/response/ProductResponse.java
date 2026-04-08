package com.example.recommendershop.dto.product.response;

import com.example.recommendershop.dto.category.response.CategoryResponse;
import com.example.recommendershop.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private UUID productId;
    private String name;
    private String description;
    private int quantity;
    private String color;
    private String mainImage;
    private List<String> image;
    private Double price;
    private Integer age;
    private Sex sex;
    private CategoryResponse category;
}
