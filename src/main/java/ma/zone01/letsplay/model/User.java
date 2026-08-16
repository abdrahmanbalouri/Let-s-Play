package ma.zone01.letsplay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @JsonIgnore
    @Field("password")
    private String password;

    /** "ROLE_USER" or "ROLE_ADMIN" */
    @Field("role")
    private String role;

    public User() {}

    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Builder
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, name, email, password, role;
        public Builder id(String id)           { this.id = id; return this; }
        public Builder name(String name)       { this.name = name; return this; }
        public Builder email(String email)     { this.email = email; return this; }
        public Builder password(String p)      { this.password = p; return this; }
        public Builder role(String role)       { this.role = role; return this; }
        public User build() { return new User(id, name, email, password, role); }
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public String getRole()     { return role; }

    public void setId(String id)           { this.id = id; }
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(String role)       { this.role = role; }
}
