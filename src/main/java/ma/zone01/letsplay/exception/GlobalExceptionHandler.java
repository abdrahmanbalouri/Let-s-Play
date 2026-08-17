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
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Method 1:
    // Logger used to record technical errors and debugging information.
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Method 2:
    // Custom exception used when a requested resource does not exist.
    // HTTP status: 404 NOT FOUND.
    public static class ResourceNotFoundException
            extends RuntimeException {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Method 3:
    // Custom exception used when the request conflicts with existing data.
    // Example: duplicate email.
    // HTTP status: 409 CONFLICT.
    public static class ConflictException
            extends RuntimeException {

        public ConflictException(String message) {
            super(message);
        }
    }

    // Method 4:
    // Custom exception used when the authenticated user
    // is not allowed to perform a business operation.
    // HTTP status: 403 FORBIDDEN.
    public static class ForbiddenException
            extends RuntimeException {

        public ForbiddenException(String message) {
            super(message);
        }
    }

    // Method 5:
    // Handles ResourceNotFoundException.
    // Used when a resource such as a user or product does not exist.
    // HTTP status: 404 NOT FOUND.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(
            ResourceNotFoundException ex) {

        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    // Method 6:
    // Handles ConflictException.
    // Used when the request conflicts with existing application data.
    // HTTP status: 409 CONFLICT.
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse> handleConflict(
            ConflictException ex) {

        return error(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    // Method 7:
    // Handles the custom ForbiddenException.
    // Used when business logic denies the operation.
    // HTTP status: 403 FORBIDDEN.
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbidden(
            ForbiddenException ex) {

        return error(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    // Method 8:
    // Handles Spring Security AccessDeniedException.
    // Happens when an authenticated user does not have
    // the required role or authority.
    // HTTP status: 403 FORBIDDEN.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(
            AccessDeniedException ex) {

        return error(
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
    }

    // Method 9:
    // Handles authentication failures caused by invalid credentials.
    // Example: wrong email or password.
    // HTTP status: 401 UNAUTHORIZED.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(
            BadCredentialsException ex) {

        return error(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }

    // Method 10:
    // Handles validation errors produced by @Valid.
    // Example: @NotBlank, @NotNull, @Positive.
    // Collects errors for each invalid field.
    // HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Collect each field name with its validation message.
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

    // Method 11:
    // Handles unreadable or malformed request bodies.
    // Example: invalid JSON or wrong JSON data type.
    // HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleUnreadable(
            HttpMessageNotReadableException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                "Malformed request body"
        );
    }

    // Method 12:
    // Handles database integrity violations.
    // Example: duplicate unique value.
    // Technical details are logged on the server.
    // HTTP status: 409 CONFLICT.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        log.warn(
                "Data integrity violation: {}",
                ex.getMessage()
        );

        return error(
                HttpStatus.CONFLICT,
                "Data integrity violation"
        );
    }

    // Method 13:
    // Handles IllegalArgumentException thrown by application code.
    // Example: invalid price or invalid parameter.
    // HTTP status: 400 BAD REQUEST.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    // Method 14:
    // Handles requests that use an HTTP method not supported
    // by the requested endpoint.
    // Example: sending PUT to an endpoint that only supports GET.
    // HTTP status: 405 METHOD NOT ALLOWED.
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {

        String method = ex.getMethod();

        return error(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method " + method + " not allowed for this endpoint"
        );
    }

    // Method 15:
    // Fallback handler for unexpected exceptions.
    // Used when no specific exception handler matches.
    // Technical details are logged on the server.
    // HTTP status: 500 INTERNAL SERVER ERROR.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(
            Exception ex) {

        log.error(
                "Unhandled exception: {}",
                ex.getMessage(),
                ex
        );

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    // Method 16:
    // Common helper method used by all exception handlers.
    // Builds a consistent API response with the given status and message.
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