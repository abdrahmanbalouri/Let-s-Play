package ma.zone01.letsplay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public void setName(String name)    { this.name = name; }
    public void setEmail(String email)  { this.email = email; }
    public void setPassword(String p)   { this.password = p; }
}
