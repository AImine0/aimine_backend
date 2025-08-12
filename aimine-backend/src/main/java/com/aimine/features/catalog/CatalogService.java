package com.aimine.features.catalog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {
    private final AiServiceRepository repo;

    public CatalogService(AiServiceRepository r) {
        this.repo = r;
    }

    public List<AiService> list() {
        return repo.findAll();
    }
}