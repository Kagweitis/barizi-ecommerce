package com.barizi.ecommerce.barizi.Services;


import com.barizi.ecommerce.barizi.DTOs.Response.ProductResponse.CategoryResponse;
import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResponseEntity<CategoryResponse> addCategory(Category category){

        CategoryResponse res = new CategoryResponse();
        try {
            if (category.getName() == null || category.getDescription() == null){
                res.setMessage("Please ensure the category has a name and description");
                res.setStatusCode(400);
                return ResponseEntity.status(res.getStatusCode()).body(res);
            }

            if (categoryRepository.existsByName(category.getName())){
                res.setMessage("A category with that name already exists. Please update it");
                res.setStatusCode(409);
                return ResponseEntity.status(res.getStatusCode()).body(res);
            }

            categoryRepository.save(category);
            res.setMessage("Successfully added category");
            res.setStatusCode(200);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        } catch (Exception e){
            log.error("error adding category "+e);
            res.setMessage("Couldn't add the category. Please try again later");
            res.setStatusCode(500);
            return ResponseEntity.status(res.getStatusCode()).body(res);
        }
    }
}
