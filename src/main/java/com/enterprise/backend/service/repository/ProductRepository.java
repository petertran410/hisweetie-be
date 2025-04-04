package com.enterprise.backend.service.repository;

import com.enterprise.backend.model.entity.Product;
import com.enterprise.backend.service.base.BaseCommonRepository;

public interface ProductRepository extends BaseCommonRepository<Product, Long> {
    Integer countByTitle(String title);
}
