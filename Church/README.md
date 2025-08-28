# Product API - Cassandra Schema Design

## Overview
This document describes the Cassandra schema design and API endpoints for the Product management system.

## Cassandra Schema Design

### Products Table
```sql
CREATE TABLE IF NOT EXISTS church_products.products (
    id UUID PRIMARY KEY,
    tenant_id UUID,
    type TEXT,
    name TEXT,
    description TEXT
);

-- Create secondary indexes for efficient queries
CREATE INDEX IF NOT EXISTS ON church_products.products (tenant_id);
CREATE INDEX IF NOT EXISTS ON church_products.products (type);
```

### Schema Rationale

1. **Primary Key**: `id` (UUID) - Unique identifier for each product
2. **Secondary Indexes**: 
   - `tenant_id` - Enables efficient filtering by tenant
   - `type` - Enables efficient filtering by product type

3. **Design Considerations**:
   - **Tenant Isolation**: All queries are filtered by `tenant_id` to ensure data isolation
   - **Type-based Access**: Products can be uniquely identified within a tenant by their `type`
   - **UUID as Primary Key**: Ensures globally unique identifiers and good distribution across partitions
   - **ALLOW FILTERING**: Used in queries for flexibility, though it may have performance implications at scale

### Recommended Production Optimizations

For production environments with high scale, consider these optimizations:

1. **Composite Primary Key**:
   ```sql
   CREATE TABLE products (
       tenant_id UUID,
       type TEXT,
       id UUID,
       name TEXT,
       description TEXT,
       PRIMARY KEY (tenant_id, type, id)
   );
   ```
   - Partitioning by `tenant_id` ensures tenant data is co-located
   - Clustering by `type` and `id` enables efficient type-based queries
   - Eliminates need for ALLOW FILTERING

2. **Materialized Views** for different access patterns:
   ```sql
   CREATE MATERIALIZED VIEW products_by_tenant AS
   SELECT * FROM products
   WHERE tenant_id IS NOT NULL AND type IS NOT NULL AND id IS NOT NULL
   PRIMARY KEY (tenant_id, id);
   ```

## API Endpoints

### Authentication
All endpoints require the `x-tenant-user-token` header containing a Base64-encoded token in the format:
```
Base64(tenantId:role:userId)
```

**Supported Roles:**
- `SYSTEM_ADMINISTRATOR` - Global administrative access
- `ADMINISTRATOR` - Tenant-level administrative access  
- `OPERATOR` - Tenant-level operational access
- `USER` - Basic user access (read-only for most operations)

### Endpoints

#### 1. Create Product
```http
POST /products
Content-Type: application/json
x-tenant-user-token: <base64-encoded-token>

{
    "type": "electronics",
    "name": "Smartphone",
    "description": "Latest model smartphone"
}
```

**Authorization**: Requires SYSTEM_ADMINISTRATOR, ADMINISTRATOR, or OPERATOR role

**Response**:
```json
{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "tenantId": "789e4567-e89b-12d3-a456-426614174001",
    "type": "electronics",
    "name": "Smartphone",
    "description": "Latest model smartphone"
}
```

#### 2. Get All Products (by Tenant)
```http
GET /products
x-tenant-user-token: <base64-encoded-token>
```

**Authorization**: Any authenticated user with valid tenant token

**Response**:
```json
[
    {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "tenantId": "789e4567-e89b-12d3-a456-426614174001",
        "type": "electronics",
        "name": "Smartphone",
        "description": "Latest model smartphone"
    }
]
```

#### 3. Get Product by Type
```http
GET /products/{type}
x-tenant-user-token: <base64-encoded-token>
```

**Authorization**: Any authenticated user with valid tenant token

#### 4. Update Product by Type
```http
PUT /products/{type}
Content-Type: application/json
x-tenant-user-token: <base64-encoded-token>

{
    "name": "Updated Smartphone",
    "description": "Updated description"
}
```

**Authorization**: Requires SYSTEM_ADMINISTRATOR, ADMINISTRATOR, or OPERATOR role

#### 5. Delete Product by Type
```http
DELETE /products/{type}
x-tenant-user-token: <base64-encoded-token>
```

**Authorization**: Requires SYSTEM_ADMINISTRATOR, ADMINISTRATOR, or OPERATOR role

## Token Generation Example

To generate a valid token for testing:

```bash
# For a tenant admin
echo -n "789e4567-e89b-12d3-a456-426614174001:ADMINISTRATOR:admin123" | base64

# For a system admin  
echo -n "789e4567-e89b-12d3-a456-426614174001:SYSTEM_ADMINISTRATOR:sysadmin" | base64

# For an operator
echo -n "789e4567-e89b-12d3-a456-426614174001:OPERATOR:operator123" | base64
```

## Error Responses

### 401 Unauthorized
```json
"Invalid token: <error-message>"
```

### 403 Forbidden
```json
"Access denied. Required roles: System Administrator, Administrator, or Operator"
```

### 404 Not Found
```json
"Product not found for type: <type>"
```

### 500 Internal Server Error
```json
"Internal server error"
```

## Configuration

### Cassandra Configuration (application.properties)
```properties
spring.cassandra.keyspace-name=church_products
spring.cassandra.contact-points=127.0.0.1
spring.cassandra.port=9042
spring.cassandra.local-datacenter=datacenter1
spring.cassandra.schema-action=create_if_not_exists
```

### Production Considerations

1. **Security**: 
   - Implement proper JWT token validation instead of Base64 encoding
   - Use HTTPS for all communications
   - Implement rate limiting
   
2. **Performance**:
   - Use prepared statements
   - Implement connection pooling
   - Consider caching frequently accessed data
   
3. **Monitoring**:
   - Add metrics for API response times
   - Monitor Cassandra cluster health
   - Implement distributed tracing