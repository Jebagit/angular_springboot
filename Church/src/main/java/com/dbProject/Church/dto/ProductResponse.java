package com.dbProject.Church.dto;

import com.dbProject.Church.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private UUID id;
    private UUID tenantId;
    private String type;
    private String name;
    private String description;
    
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getTenantId(),
            product.getType(),
            product.getName(),
            product.getDescription()
        );
    }
}