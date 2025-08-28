package com.dbProject.Church.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("products")
public class Product {
    
    @PrimaryKey
    private UUID id;
    
    private UUID tenantId;
    
    private String type;
    
    private String name;
    
    private String description;
    
    public Product(UUID tenantId, String type, String name, String description) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.type = type;
        this.name = name;
        this.description = description;
    }
}