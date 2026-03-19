package com.example.recommendershop.controller;


import com.example.recommendershop.dto.ApiListBaseRequest;
import com.example.recommendershop.dto.BasePage;
import com.example.recommendershop.dto.category.request.CategoryRequest;
import com.example.recommendershop.dto.category.response.CategoryAvatar;
import com.example.recommendershop.dto.category.response.CategoryResponse;
import com.example.recommendershop.service.category.CategoryService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/")
    public CategoryResponse add(@RequestBody CategoryRequest categoryRequest){
        return categoryService.create(categoryRequest);
    }
    @PatchMapping("/{categoryId}")
    public CategoryResponse edit(@PathVariable(name = "categoryId")UUID categoryId, @RequestBody CategoryRequest categoryRequest){
        return categoryService.update(categoryId, categoryRequest);
    }
    @GetMapping("/{categoryId}")
    public CategoryResponse getById(@PathVariable(name = "categoryId") UUID categoryId){
        return categoryService.detail(categoryId);
    }
    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable(name = "categoryId") UUID categoryId){
        categoryService.delete(categoryId);
    }
    @CrossOrigin
    @GetMapping("/")
    public BasePage<CategoryAvatar> findAll(@ParameterObject ApiListBaseRequest listBaseRequest){
        return categoryService.getAll(listBaseRequest);
    }

}
