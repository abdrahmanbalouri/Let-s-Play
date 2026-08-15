package ma.zone01.letsplay.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private Double price;
    private String userId;

    public Product() {}

    public Product(String id, String name, String description, Double price, String userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, name, description, userId;
        private Double price;
        public Builder id(String id)                  { this.id = id; return this; }
        public Builder name(String name)              { this.name = name; return this; }
        public Builder description(String desc)       { this.description = desc; return this; }
        public Builder price(Double price)            { this.price = price; return this; }
        public Builder userId(String userId)          { this.userId = userId; return this; }
        public Product build() { return new Product(id, name, description, price, userId); }
    }

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public Double getPrice()       { return price; }
    public String getUserId()      { return userId; }

    public void setId(String id)                  { this.id = id; }
    public void setName(String name)              { this.name = name; }
    public void setDescription(String description){ this.description = description; }
    public void setPrice(Double price)            { this.price = price; }
    public void setUserId(String userId)          { this.userId = userId; }
}
