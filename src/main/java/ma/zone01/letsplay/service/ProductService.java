package ma.zone01.letsplay.service;

import ma.zone01.letsplay.dto.request.ProductRequest;
import ma.zone01.letsplay.exception.ForbiddenException;
import ma.zone01.letsplay.exception.ResourceNotFoundException;
import ma.zone01.letsplay.model.Product;
import ma.zone01.letsplay.model.User;
import ma.zone01.letsplay.repository.ProductRepository;
import ma.zone01.letsplay.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return findProductById(id);
    }

    public Product createProduct(ProductRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .userId(user.getId())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductRequest request, String userEmail, boolean isAdmin) {
        Product product = findProductById(id);
        checkOwnership(product, userEmail, isAdmin);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return productRepository.save(product);
    }

    public void deleteProduct(String id, String userEmail, boolean isAdmin) {
        Product product = findProductById(id);
        checkOwnership(product, userEmail, isAdmin);
        productRepository.delete(product);
    }

    private void checkOwnership(Product product, String userEmail, boolean isAdmin) {
        if (isAdmin) return;

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        if (!product.getUserId().equals(user.getId())) {
            throw new ForbiddenException("You do not own this product");
        }
    }

    private Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
