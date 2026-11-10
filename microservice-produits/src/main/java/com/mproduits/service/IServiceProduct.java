package com.mproduits.service;

import com.mproduits.model.Product;

import java.util.List;
import java.util.Optional;

public interface IServiceProduct {
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    // List<Product> findByName(String name);
    void save(Product product);
    void deleteById(Integer id);
    Product update(Product product);
}
