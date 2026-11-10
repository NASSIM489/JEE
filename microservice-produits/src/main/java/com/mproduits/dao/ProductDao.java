package com.mproduits.dao;

import com.mproduits.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Repository est une annotation Spring pour indiquer que la classe est un bean,
 * et que son rôle est de communiquer avec une source de données (en l'occurrence la base de données).
 * @Repository est une spécialisation de @Component.
 * Tout comme @Component, elle permet de déclarer auprès de Spring qu'une classe est un bean à exploiter.
 */
@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
}
