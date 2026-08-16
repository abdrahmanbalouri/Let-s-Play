package ma.zone01.letsplay.exception;

import ma.zone01.letsplay.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Method 1: Logger used to record errors and useful debugging information.
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // Method 2: Custom exception used when a requested resource does not exist.
    // Example: Product with the given ID was not found.
    // Final HTTP status: 404 NOT FOUND.
    public static class ResourceNotFoundException
            extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }


    // Method 3: Custom exception used when the request conflicts
    // with existing data or application state.
    // Example: an email already exists.
    // Final HTTP status: 409 CONFLICT.
    public static class ConflictException
            extends RuntimeException {

        public ConflictException(String message) {
            super(message);
        }
    }


    // Method 4: Custom exception used when a user is authenticated
    // but is not allowed to perform a specific business operation.
    // Example: a user tries to modify another user's product.
    // Final HTTP status: 403 FORBIDDEN.
    public static class ForbiddenException
            extends RuntimeException {

        public ForbiddenException(String message) {
            super(message);
        }
    }


    // Method 5: Handles ResourceNotFoundException.
    // Returns 404 when the requested resource does not exist.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(
            ResourceNotFoundException ex) {

        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }


    // Method 6: Handles ConflictException.
    // Returns 409 when the request conflicts with existing data.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse> handleConflict(
            ConflictException ex) {

        return error(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }


    // Method 7: Handles the custom ForbiddenException.
    // Returns 403 when the application business logic denies the action.
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbidden(
            ForbiddenException ex) {

        return error(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }


    // Method 8: Handles Spring Security's AccessDeniedException.
    // This happens when an authenticated user does not have the required authority.
    // Example: @PreAuthorize("hasRole('ADMIN')") rejects a normal user.
    // Final HTTP status: 403 FORBIDDEN.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(
            AccessDeniedException ex) {

        return error(
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
    }


    // Method 9: Handles BadCredentialsException.
    // Usually happens during authentication when the email or password is invalid.
    // Final HTTP status: 401 UNAUTHORIZED.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(
            BadCredentialsException ex) {

        return error(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }


    // Method 10: Handles validation errors produced by @Valid.
    // Example: @NotBlank, @NotNull, @Positive, etc.
    // Final HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Collect the validation error message for each invalid field.
        for (FieldError fe :
                ex.getBindingResult().getFieldErrors()) {

            fieldErrors.put(
                    fe.getField(),
                    fe.getDefaultMessage()
            );
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.success(
                                "Validation failed",
                                fieldErrors
                        )
                );
    }


    // Method 11: Handles requests whose body cannot be read or parsed.
    // Example: malformed JSON or an invalid JSON value/type.
    // Final HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleUnreadable(
            HttpMessageNotReadableException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                "Malformed request body"
        );
    }


    // Method 12: Handles database integrity/constraint violations.
    // Example: duplicate unique value or another database constraint failure.
    // Final HTTP status: 409 CONFLICT.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        // Log the technical database error for developers.
        log.warn(
                "Data integrity violation: {}",
                ex.getMessage()
        );

        // Do not expose database details to the client.
        return error(
                HttpStatus.CONFLICT,
                "Data integrity violation"
        );
    }


    // Method 13: Handles invalid arguments explicitly thrown by application code.
    // Example: throw new IllegalArgumentException("Invalid price");
    // Final HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }


    // Method 14: Fallback handler for unexpected exceptions.
    // Used when no more specific exception handler exists.
    // Final HTTP status: 500 INTERNAL SERVER ERROR.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(
            Exception ex) {

        // Keep the full technical error in the server logs.
        log.error(
                "Unhandled exception: {}",
                ex.getMessage(),
                ex
        );

        // Return a generic message to the client.
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }


    // Method 15: Common helper used by the other handlers.
    // Builds a consistent HTTP response with the given status and message.
    private ResponseEntity<ApiResponse> error(
            HttpStatus status,
            String message) {

        return ResponseEntity
                .status(status)
                .body(
                        ApiResponse.success(message)
                );
    }
}