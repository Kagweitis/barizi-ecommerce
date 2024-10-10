package com.barizi.ecommerce.barizi.controllers;


import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.CategoryResponse;
import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Services.CategoryService;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/categories")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class CategoriesController {

    private final CategoryService categoryService;


    @PostMapping("new/category")
    public ResponseEntity<CategoryResponse> addNewCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }
}
