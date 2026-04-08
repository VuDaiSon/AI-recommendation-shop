package com.example.recommendershop.dto.product.request;

import com.example.recommendershop.enums.Sex;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private int quantity;
    @NotNull
    private String mainImage;
    @NotNull
    private List<String> image;
    @NotNull
    private String color;
    @NotNull
    private Double price;
    @NotNull
    private Integer age;
    @NotNull
    private Sex sex;
    @NotNull
    private UUID categoryId;
}
