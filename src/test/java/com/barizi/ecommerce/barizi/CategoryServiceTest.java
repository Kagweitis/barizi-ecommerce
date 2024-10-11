package com.barizi.ecommerce.barizi;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.CategoryResponse;
import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Repositories.CategoryRepository;
import com.barizi.ecommerce.barizi.Services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCategory_MissingNameOrDescription() {
        // Arrange
        Category category = new Category(); // Name and Description are null
        CategoryResponse expectedResponse = new CategoryResponse();
        expectedResponse.setMessage("Please ensure the category has a name and description");
        expectedResponse.setStatusCode(400);

        // Act
        ResponseEntity<CategoryResponse> response = categoryService.addCategory(category);

        // Assert
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        assertEquals(expectedResponse.getStatusCode(), response.getBody().getStatusCode());
    }

    @Test
    public void testAddCategory_AlreadyExists() {
        // Arrange
        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic gadgets");

        when(categoryRepository.existsByName(category.getName())).thenReturn(true);
        CategoryResponse expectedResponse = new CategoryResponse();
        expectedResponse.setMessage("A category with that name already exists. Please update it");
        expectedResponse.setStatusCode(409);

        // Act
        ResponseEntity<CategoryResponse> response = categoryService.addCategory(category);

        // Assert
        assertEquals(CONFLICT, response.getStatusCode());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        assertEquals(expectedResponse.getStatusCode(), response.getBody().getStatusCode());

        verify(categoryRepository, times(1)).existsByName(category.getName());
        verify(categoryRepository, never()).save(any(Category.class)); // Ensure save is never called
    }

    @Test
    public void testAddCategory_SuccessfullyAdded() {
        // Arrange
        Category category = new Category();
        category.setName("Fashion");
        category.setDescription("Fashion accessories");

        when(categoryRepository.existsByName(category.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        CategoryResponse expectedResponse = new CategoryResponse();
        expectedResponse.setMessage("Successfully added category");
        expectedResponse.setStatusCode(200);

        // Act
        ResponseEntity<CategoryResponse> response = categoryService.addCategory(category);

        // Assert
        assertEquals(OK, response.getStatusCode());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        assertEquals(expectedResponse.getStatusCode(), response.getBody().getStatusCode());

        verify(categoryRepository, times(1)).existsByName(category.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testAddCategory_ExceptionThrown() {
        // Arrange
        Category category = new Category();
        category.setName("Toys");
        category.setDescription("Children's toys");

        when(categoryRepository.existsByName(category.getName())).thenThrow(new RuntimeException("Database error"));
        CategoryResponse expectedResponse = new CategoryResponse();
        expectedResponse.setMessage("Couldn't add the category. Please try again later");
        expectedResponse.setStatusCode(500);

        // Act
        ResponseEntity<CategoryResponse> response = categoryService.addCategory(category);

        // Assert
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedResponse.getMessage(), response.getBody().getMessage());
        assertEquals(expectedResponse.getStatusCode(), response.getBody().getStatusCode());

        verify(categoryRepository, times(1)).existsByName(category.getName());
        verify(categoryRepository, never()).save(any(Category.class)); // Ensure save is never called
    }

}