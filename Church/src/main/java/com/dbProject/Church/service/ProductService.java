package com.dbProject.Church.service;

import com.dbProject.Church.model.Product;
import com.dbProject.Church.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public Product createProduct(UUID tenantId, String type, String name, String description) {
        Product product = new Product(tenantId, type, name, description);
        return productRepository.save(product);
    }
    
    public List<Product> getAllProductsByTenant(UUID tenantId) {
        return productRepository.findByTenantId(tenantId);
    }
    
    public Optional<Product> getProductByTenantAndType(UUID tenantId, String type) {
        return productRepository.findByTenantIdAndType(tenantId, type);
    }
    
    public Optional<Product> updateProductByType(UUID tenantId, String type, String name, String description) {
        Optional<Product> existingProduct = productRepository.findByTenantIdAndType(tenantId, type);
        
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(name);
            product.setDescription(description);
            return Optional.of(productRepository.save(product));
        }
        return Optional.empty();
    }
    
    public boolean deleteProductByType(UUID tenantId, String type) {
        Optional<Product> existingProduct = productRepository.findByTenantIdAndType(tenantId, type);
        if (existingProduct.isPresent()) {
            productRepository.deleteByTenantIdAndType(tenantId, type);
            return true;
        }
        return false;
    }
}