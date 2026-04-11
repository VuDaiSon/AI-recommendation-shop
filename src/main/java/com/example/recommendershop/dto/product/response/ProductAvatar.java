package com.example.recommendershop.dto.product.response;

import com.example.recommendershop.dto.BaseDto;
import com.example.recommendershop.dto.category.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductAvatar extends BaseDto {
    private UUID productId;
    private String name;
    private int quantity;
    private String mainImage;
    private Double price;
    private String color;
    private Date date;
    private CategoryResponse category;
}
