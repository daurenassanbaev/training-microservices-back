package com.daurenassanbaev.productservice.database.repository;

import com.daurenassanbaev.productservice.database.entity.Product;
import com.daurenassanbaev.productservice.dto.ProductFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class FilterProductRepositoryImpl implements FilterProductRepository {
    private final EntityManager entityManager;

    @Override
    public List<Product> findAllByFilter(ProductFilter filter) {
        var cb = entityManager.getCriteriaBuilder();
        var criteria = cb.createQuery(Product.class);
        var product = criteria.from(Product.class);
        criteria.select(product);

        List<Predicate> predicates = new ArrayList<>();

        if (filter.name() != null && !filter.name().isBlank()) {
            predicates.add((Predicate) cb.like(product.get("name"), "%" + filter.name() + "%"));
        }

        if (filter.minPrice() != null && !filter.name().isBlank()) {
            predicates.add((Predicate)cb.greaterThanOrEqualTo(product.get("price"), filter.minPrice()));
        }

        if (filter.maxPrice() != null &&  !filter.name().isBlank()) {
            predicates.add((Predicate)cb.lessThanOrEqualTo(product.get("price"), filter.maxPrice()));
        }

        criteria.where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        return entityManager.createQuery(criteria).getResultList();
    }
}

