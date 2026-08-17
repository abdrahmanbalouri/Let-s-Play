package ma.zone01.letsplay.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import ma.zone01.letsplay.dto.request.ProductRequest;
import ma.zone01.letsplay.dto.response.ApiResponse;
import ma.zone01.letsplay.model.Product;
import ma.zone01.letsplay.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PermitAll
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @PermitAll
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request, auth.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductRequest request,
            Authentication auth) {
        Product updated = productService.updateProduct(id, request, auth.getName(), isAdmin(auth));
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable String id,
            Authentication auth) {
        productService.deleteProduct(id, auth.getName(), isAdmin(auth));
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }
}
