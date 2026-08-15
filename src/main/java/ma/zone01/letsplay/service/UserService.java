package ma.zone01.letsplay.service;

import ma.zone01.letsplay.dto.request.UpdateUserRequest;
import ma.zone01.letsplay.dto.response.UserResponse;
import ma.zone01.letsplay.exception.ConflictException;
import ma.zone01.letsplay.exception.ResourceNotFoundException;
import ma.zone01.letsplay.model.User;
import ma.zone01.letsplay.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        return UserResponse.from(findUserById(id));
    }

    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (StringUtils.hasText(request.getName())) {
            user.setName(request.getName());
        }

        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already in use: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return UserResponse.from(user);
    }

    public void deleteUser(String id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
