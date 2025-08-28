#!/bin/bash

# Product API Test Script
# This script demonstrates how to use the Product API endpoints
# Prerequisites: 
# 1. Cassandra running on localhost:9042
# 2. Spring Boot application running on localhost:8080

BASE_URL="http://localhost:8080"
TENANT_ID="789e4567-e89b-12d3-a456-426614174001"

# Generate tokens for different roles
ADMIN_TOKEN=$(echo -n "${TENANT_ID}:ADMINISTRATOR:admin123" | base64)
OPERATOR_TOKEN=$(echo -n "${TENANT_ID}:OPERATOR:operator123" | base64)
USER_TOKEN=$(echo -n "${TENANT_ID}:USER:user123" | base64)

echo "=== Product API Test Script ==="
echo "Base URL: $BASE_URL"
echo "Tenant ID: $TENANT_ID"
echo ""

# Test 1: Create a product (requires admin role)
echo "1. Creating a product (as Administrator)..."
CREATE_RESPONSE=$(curl -s -X POST "${BASE_URL}/products" \
  -H "Content-Type: application/json" \
  -H "x-tenant-user-token: $ADMIN_TOKEN" \
  -d '{
    "type": "electronics",
    "name": "Smartphone",
    "description": "Latest model smartphone"
  }')
echo "Response: $CREATE_RESPONSE"
echo ""

# Test 2: Create another product
echo "2. Creating another product (as Operator)..."
curl -s -X POST "${BASE_URL}/products" \
  -H "Content-Type: application/json" \
  -H "x-tenant-user-token: $OPERATOR_TOKEN" \
  -d '{
    "type": "furniture",
    "name": "Office Chair",
    "description": "Ergonomic office chair"
  }'
echo ""

# Test 3: Get all products
echo "3. Getting all products (as User)..."
curl -s -X GET "${BASE_URL}/products" \
  -H "x-tenant-user-token: $USER_TOKEN" | jq '.'
echo ""

# Test 4: Get product by type
echo "4. Getting product by type 'electronics'..."
curl -s -X GET "${BASE_URL}/products/electronics" \
  -H "x-tenant-user-token: $USER_TOKEN" | jq '.'
echo ""

# Test 5: Update product (requires admin role)
echo "5. Updating product 'electronics' (as Administrator)..."
curl -s -X PUT "${BASE_URL}/products/electronics" \
  -H "Content-Type: application/json" \
  -H "x-tenant-user-token: $ADMIN_TOKEN" \
  -d '{
    "name": "Updated Smartphone",
    "description": "Latest model smartphone with updates"
  }' | jq '.'
echo ""

# Test 6: Try to create product as user (should fail)
echo "6. Attempting to create product as User (should fail)..."
curl -s -X POST "${BASE_URL}/products" \
  -H "Content-Type: application/json" \
  -H "x-tenant-user-token: $USER_TOKEN" \
  -d '{
    "type": "test",
    "name": "Test Product",
    "description": "This should fail"
  }'
echo ""

# Test 7: Delete product (requires admin role)
echo "7. Deleting product 'furniture' (as Administrator)..."
curl -s -X DELETE "${BASE_URL}/products/furniture" \
  -H "x-tenant-user-token: $ADMIN_TOKEN"
echo ""

# Test 8: Verify deletion
echo "8. Verifying deletion - getting all products..."
curl -s -X GET "${BASE_URL}/products" \
  -H "x-tenant-user-token: $USER_TOKEN" | jq '.'
echo ""

echo "=== Test script completed ==="