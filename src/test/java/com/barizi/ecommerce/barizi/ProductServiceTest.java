package com.barizi.ecommerce.barizi;


import com.barizi.ecommerce.barizi.DTOs.Request.ProductRequests.ProductRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.ProductResponse;
import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Entities.Product;
import com.barizi.ecommerce.barizi.Repositories.CategoryRepository;
import com.barizi.ecommerce.barizi.Repositories.ProductRepository;
import com.barizi.ecommerce.barizi.Repositories.UserRepository;
import com.barizi.ecommerce.barizi.Services.OrderService;
import com.barizi.ecommerce.barizi.Services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {


    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private OrderService orderService;

    @InjectMocks
    private ProductService productService;

    @Test
    void testNewProduct_Success() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("This is a test product");
        productRequest.setPrice(100);
        productRequest.setStock(10);
        productRequest.setCategoryId(1);

        Product product = new Product();
        product.setName("Test Product");

        Category category = new Category();
        category.setId(1L);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(productRepository.findByNameAndDeleted("Test Product")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ProductResponse> response = productService.newProduct(productRequest);

        // Assert
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Product added successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getProduct());
    }

    @Test
    void testNewProduct_MissingName() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setDescription("This is a test product");
        productRequest.setPrice(100);
        productRequest.setStock(10);
        productRequest.setCategoryId(1);

        // Act
        ResponseEntity<ProductResponse> response = productService.newProduct(productRequest);

        // Assert
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Product name is mandatory", response.getBody().getMessage());
    }


    @Test
    void testNewProduct_MissingDescription() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setPrice(100);
        productRequest.setStock(10);
        productRequest.setCategoryId(1);

        // Act
        ResponseEntity<ProductResponse> response = productService.newProduct(productRequest);

        // Assert
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Description is mandatory", response.getBody().getMessage());
    }


    @Test
    void testNewProduct_InvalidPrice() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("This is a test product");
        productRequest.setPrice(0);
        productRequest.setStock(10);
        productRequest.setCategoryId(1);

        // Act
        ResponseEntity<ProductResponse> response = productService.newProduct(productRequest);

        // Assert
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Price must be greater than zero", response.getBody().getMessage());
    }

    @Test
    void testNewProduct_DuplicateName() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("This is a test product");
        productRequest.setPrice(100);
        productRequest.setStock(10);
        productRequest.setCategoryId(1);

        Product existingProduct = new Product();
        existingProduct.setName("Test Product");

        Mockito.when(productRepository.findByNameAndDeleted("Test Product")).thenReturn(Optional.of(existingProduct));

        // Act
        ResponseEntity<ProductResponse> response = productService.newProduct(productRequest);

        // Assert
        assertEquals(409, response.getBody().getStatusCode());
        assertEquals("Product with that name already exists", response.getBody().getMessage());
    }

    @Test
    void testNewProduct_CategoryNotFound() {
        // Arrange
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("This is a test product");
        productRequest.setPrice(100);
        productRequest.setStock(10);
        productRequest.setCategoryId(99); // non-existing category

        Mockito.when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AssertionError.class, () -> {
            productService.newProduct(productRequest);
        });
    }


}
