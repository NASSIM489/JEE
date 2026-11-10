package com.mproduits.service;

import com.mproduits.dao.ProductDao;
import com.mproduits.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceProduct implements IServiceProduct, CommandLineRunner {

    @Autowired
    private ProductDao productDao;

    @Override
    public Optional<Product> findById(Integer id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public void save(Product product) {
        productDao.save(product);
    }

    @Override
    public void deleteById(Integer id) {
        productDao.deleteById(id);
    }

    @Override
    public Product update(Product product) {
        return productDao.save(product);
    }

    @Override
    public void run(String... args) throws Exception {
        productDao.deleteAll();
        productDao.save(new Product("titre1", "description1", 1));
        productDao.save(new Product("titre2", "description2", 2));
        productDao.save(new Product("titre3", "description3", 3));
        productDao.save(new Product("titre4", "description4", 4));
        productDao.save(new Product("titre5", "description5", 5));
        productDao.save(new Product("titre6", "description6", 6));
        productDao.save(new Product("titre7", "description7", 7));
        productDao.save(new Product("titre8", "description8", 8));
        productDao.save(new Product("titre9", "description9", 9));
        productDao.save(new Product("titre11", "description10", 10));
        productDao.save(new Product("titre12", "description12", 11));
        productDao.save(new Product("titre13", "description12", 12));
    }
}
