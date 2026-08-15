package ma.zone01.letsplay.repository;

import ma.zone01.letsplay.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserId(String userId);
}
