package com.dbProject.Church.repository;

import com.dbProject.Church.model.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends CassandraRepository<Product, UUID> {
    
    @Query("SELECT * FROM products WHERE tenantId = ?0 ALLOW FILTERING")
    List<Product> findByTenantId(UUID tenantId);
    
    @Query("SELECT * FROM products WHERE tenantId = ?0 AND type = ?1 ALLOW FILTERING")
    Optional<Product> findByTenantIdAndType(UUID tenantId, String type);
    
    @Query("DELETE FROM products WHERE tenantId = ?0 AND type = ?1")
    void deleteByTenantIdAndType(UUID tenantId, String type);
}