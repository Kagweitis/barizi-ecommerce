package com.barizi.ecommerce.barizi.Services;

import com.barizi.ecommerce.barizi.DTOs.Request.ProductRequests.ProductRequest;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.GetProductsResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.ProductResponse;
import com.barizi.ecommerce.barizi.DTOs.Response.SimpleResponse;
import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Entities.Product;
import com.barizi.ecommerce.barizi.Repositories.CategoryRepository;
import com.barizi.ecommerce.barizi.Repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<ProductResponse> newProduct(ProductRequest product){

        ProductResponse res = new ProductResponse();
        if (product.getName() == null || product.getName().isEmpty()) {
            res.setMessage("Product name is mandatory");
            res.setStatusCode(400);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }

        if (product.getDescription() == null || product.getDescription().isEmpty()) {
            res.setMessage("Description is mandatory");
            res.setStatusCode(400);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }

        if (product.getPrice() <= 0) {
            res.setMessage("Price must be greater than zero");
            res.setStatusCode(400);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }

        if (product.getStock() < 0) {
            res.setMessage("Stock must be zero or greater");
            res.setStatusCode(400);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }

        if (product.getCategoryId() < 0) {
            res.setMessage("Please select a category");
            res.setStatusCode(400);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }

        try {
            Optional<Product> existingProduct = productRepository.findByNameAndDeleted(product.getName());

            existingProduct.ifPresentOrElse(product1 -> {
                res.setMessage("Product with that name already exists");
                res.setStatusCode(409);
            }, ()-> {
                Optional<Category> category = categoryRepository.findById(product.getCategoryId());
                HashSet<Category> categories = new HashSet<>();
                assert category.isPresent();
                categories.add(category.get());
                Product newProduct = Product.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .description(product.getDescription())
                        .category(categories).build();
                productRepository.save(newProduct);
                res.setMessage("Product added successfully");
                res.setStatusCode(500);
                res.setProduct(newProduct);
            });
        } catch (Exception e){
            log.error("error adding product "+e);
            res.setMessage("Couldn't add the product. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);

    }

    public ResponseEntity<ProductResponse> updateProduct(ProductRequest productRequest){
        ProductResponse res = new ProductResponse();
        HashSet<Category> categories = new HashSet<>();

        try {
            Optional<Product> existingProductOptional = productRepository.findByIdDeleted(productRequest.getId());
            if (productRequest.getCategoryId() > 0){
                Optional<Category> category = categoryRepository.findById(productRequest.getCategoryId());
                category.ifPresent(categories::add);
            }
            existingProductOptional.ifPresentOrElse(product -> {
                product.setCategory(!categories.isEmpty() ? categories : product.getCategory());
                product.setName(productRequest.getName() != null ? productRequest.getName() : product.getName());
                product.setPrice(productRequest.getPrice() > 0 ? productRequest.getPrice() : product.getPrice());
                product.setDescription(productRequest.getDescription() != null ? productRequest.getDescription() : product.getDescription());
                product.setStock(productRequest.getStock() >= 0 ? productRequest.getStock() : product.getStock());
                productRepository.save(product);
                res.setMessage("Product updated successfully");
                res.setStatusCode(200);
                res.setProduct(product);
            }, () -> {
                res.setMessage("Product not found");
                res.setStatusCode(404);
            });
        } catch (Exception e){
            log.error("error adding product "+e);
            res.setMessage("Couldn't add the product. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    public ResponseEntity<SimpleResponse> deleteProduct(long id){
        SimpleResponse res = new SimpleResponse();

        try {
            Optional<Product> existingProduct = productRepository.findByIdDeleted(id);
            existingProduct.ifPresentOrElse(product -> {
                product.setDeleted(true);
                productRepository.save(product);
                res.setMessage("Product deleted successfully");
                res.setStatusCode(200);
            }, ()-> {
                res.setMessage("Product not found");
                res.setStatusCode(404);
            });
        } catch (Exception e){
            log.error("error deleting product "+e);
            res.setMessage("Couldn't deleted the product. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
        return ResponseEntity.status(res.getStatusCode()).body(res);
    }

    public ResponseEntity<GetProductsResponse> getProducts(){
        GetProductsResponse res = new GetProductsResponse();

        try {
            List<Product> products = productRepository.findAllByDeleted();
            if (products.isEmpty()){
                res.setMessage("No products available. Please try again later");
                res.setStatusCode(204);
                return ResponseEntity.status(res.getStatusCode()).body(res);
            }
            res.setProducts(products);
            res.setMessage("Products found");
            res.setStatusCode(200);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        } catch (Exception e){
            log.error("error deleting product "+e);
            res.setMessage("Couldn't get the products. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
    }

}
