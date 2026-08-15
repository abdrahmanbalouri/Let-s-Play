package ma.zone01.letsplay.dto.response;

import ma.zone01.letsplay.model.User;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;

    public UserResponse() {}

    public UserResponse(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getRole()  { return role; }

    public void setId(String id)       { this.id = id; }
    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role)   { this.role = role; }
}
