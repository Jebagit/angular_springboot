package com.dbProject.Church.controller;

import com.dbProject.Church.dto.CreateProductRequest;
import com.dbProject.Church.dto.ProductResponse;
import com.dbProject.Church.dto.UpdateProductRequest;
import com.dbProject.Church.model.Product;
import com.dbProject.Church.service.AuthenticationService;
import com.dbProject.Church.service.AuthenticationService.TenantUserInfo;
import com.dbProject.Church.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    private final AuthenticationService authenticationService;
    
    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestHeader("x-tenant-user-token") String token,
            @Valid @RequestBody CreateProductRequest request) {
        
        try {
            TenantUserInfo userInfo = authenticationService.parseToken(token);
            
            // Check if user has admin role
            if (!authenticationService.hasAdminRole(userInfo.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Required roles: System Administrator, Administrator, or Operator");
            }
            
            Product product = productService.createProduct(
                userInfo.getTenantId(),
                request.getType(),
                request.getName(),
                request.getDescription()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.from(product));
                
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestHeader("x-tenant-user-token") String token) {
        
        try {
            TenantUserInfo userInfo = authenticationService.parseToken(token);
            
            List<Product> products = productService.getAllProductsByTenant(userInfo.getTenantId());
            List<ProductResponse> response = products.stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
    
    @GetMapping("/{type}")
    public ResponseEntity<?> getProductByType(
            @RequestHeader("x-tenant-user-token") String token,
            @PathVariable String type) {
        
        try {
            TenantUserInfo userInfo = authenticationService.parseToken(token);
            
            Optional<Product> product = productService.getProductByTenantAndType(
                userInfo.getTenantId(), type);
                
            if (product.isPresent()) {
                return ResponseEntity.ok(ProductResponse.from(product.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found for type: " + type);
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
    
    @PutMapping("/{type}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader("x-tenant-user-token") String token,
            @PathVariable String type,
            @Valid @RequestBody UpdateProductRequest request) {
        
        try {
            TenantUserInfo userInfo = authenticationService.parseToken(token);
            
            // Check if user has admin role
            if (!authenticationService.hasAdminRole(userInfo.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Required roles: System Administrator, Administrator, or Operator");
            }
            
            Optional<Product> updatedProduct = productService.updateProductByType(
                userInfo.getTenantId(), 
                type, 
                request.getName(), 
                request.getDescription()
            );
            
            if (updatedProduct.isPresent()) {
                return ResponseEntity.ok(ProductResponse.from(updatedProduct.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found for type: " + type);
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
    
    @DeleteMapping("/{type}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader("x-tenant-user-token") String token,
            @PathVariable String type) {
        
        try {
            TenantUserInfo userInfo = authenticationService.parseToken(token);
            
            // Check if user has admin role
            if (!authenticationService.hasAdminRole(userInfo.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Required roles: System Administrator, Administrator, or Operator");
            }
            
            boolean deleted = productService.deleteProductByType(
                userInfo.getTenantId(), type);
            
            if (deleted) {
                return ResponseEntity.ok("Product deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found for type: " + type);
            }
            
        } catch (IllegalArgumentException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
        }
    }
}