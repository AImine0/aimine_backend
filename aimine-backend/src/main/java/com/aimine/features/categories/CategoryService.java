package com.aimine.features.categories;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final MainCategoryRepository mainRepo;

    public CategoryService(MainCategoryRepository m) {
        this.mainRepo = m;
    }

    public List<MainCategory> findAllMain() {
        return mainRepo.findAll();
    }
}