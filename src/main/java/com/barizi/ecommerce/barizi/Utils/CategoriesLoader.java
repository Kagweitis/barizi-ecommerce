package com.barizi.ecommerce.barizi.Utils;


import com.barizi.ecommerce.barizi.Entities.Category;
import com.barizi.ecommerce.barizi.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriesLoader implements CommandLineRunner {


    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Predefined tags with categories
        List<Category> predefinedCategories = Arrays.asList(
                new Category(null, "Electronics", "New and used electronics"),
                new Category(null, "Furniture", "new furniture"),
                new Category(null, "Drinks", "alcoholic and other beverages"),
                new Category(null, "Food", "Delicious foods"),
                new Category(null, "Cars", "new and used cars"),
                new Category(null, "Sports", "Sports equipment"),
                new Category(null, "Clothing", "new clothes"),
                new Category(null, "Medicine", "prescribed drugs and medicine"),
                new Category(null, "Beauty Products", "Beauty products like lotions and make up"),
                new Category(null, "Books", "books and literature"),
                new Category(null, "Constructions equipment", "Construction equipment and stuff")

        );

        // Save tags if not already present
        for (Category tag : predefinedCategories) {
            if (!categoryRepository.existsByName(tag.getName())) {
                categoryRepository.save(tag);
            }
        }
    }
}

