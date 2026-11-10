package com.mproduits.controller;

import com.mproduits.configurations.ApplicationPropertiesConfiguration;
import com.mproduits.exceptions.ProductNotFoundException;
import com.mproduits.model.Product;
import com.mproduits.service.IServiceProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RefreshScope
public class ProductController implements HealthIndicator {

    @Autowired
    private IServiceProduct serviceProduct;

    @Autowired
    private ApplicationPropertiesConfiguration appProperties;

    // Affiche la liste de tous les produits disponibles (limitée selon la configuration externalisée)
    @GetMapping(value = "/Produits")
    public List<Product> listeDesProduits() throws ProductNotFoundException {
        System.out.println(" ********* ProductController listeDesProduits() ");
        List<Product> products = serviceProduct.findAll();
        if (products.isEmpty()) {
            throw new ProductNotFoundException("Aucun produit n'est disponible à la vente");
        }
        int limit = appProperties.getLimitDeProduits();
        if (limit > 0 && limit <= products.size()) {
            return products.subList(0, limit);
        }
        return products;
    }

    // Récupérer un produit par son id
    @GetMapping(value = "/Produits/{id}")
    public Optional<Product> recupererUnProduit(@PathVariable int id) throws ProductNotFoundException {
        System.out.println(" ********* ProductController recupererUnProduit(@PathVariable int id) ");
        Optional<Product> product = serviceProduct.findById(id);
        if (!product.isPresent()) {
            throw new ProductNotFoundException("Le produit correspondant à l'id " + id + " n'existe pas");
        }
        return product;
    }

    @Override
    public Health health() {
        System.out.println("****** Actuator : ProductController health() ");
        List<Product> products = serviceProduct.findAll();
        if (products.isEmpty()) {
            return Health.down().build();
        }
        return Health.up().build();
    }
}
