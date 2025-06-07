package com.example.nesta.exception;

import com.example.nesta.exception.common.ResourceAlreadyExistsException;
import com.example.nesta.exception.common.ResourceNotFoundException;
import com.example.nesta.exception.user.UserCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                )
        );
    }

    /**
     * Handles validation errors for request bodies annotated with @Valid.
     * <p>
     * This method is triggered when Spring fails to bind and validate
     * a @RequestBody object due to constraint violations (e.g. @NotNull, @Size).
     * It specifically handles {@link org.springframework.web.bind.MethodArgumentNotValidException},
     * which occurs when a method argument annotated with @Valid is invalid.
     * <p>
     * Example use case:
     * <ul>
     *   <li>POST /api/apartments with missing required fields in the JSON body</li>
     *   <li>Validation annotations like @NotNull or @Min fail on Apartment class</li>
     * </ul>
     *
     * @param ex the exception thrown by Spring when validation fails
     * @return a structured 400 Bad Request response containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", "Validation failed",
                "details", ex.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles custom validation errors for invalid query parameters.
     * <p>
     * This method is triggered when an {@link InvalidQueryParamException} is thrown,
     * which typically occurs when a client provides a query parameter
     * that is not recognized or not allowed by the API.
     * <p>
     * Example use case:
     * <ul>
     *   <li>GET /api/rental-offers/search?unknownParam=value</li>
     *   <li>Client sends a query parameter not defined in the filter class</li>
     * </ul>
     *
     * @param ex the custom exception indicating an invalid query parameter
     * @return a structured 400 Bad Request response containing error details
     */
    @ExceptionHandler(InvalidQueryParamException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidQueryParamException(InvalidQueryParamException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 409,
                        "error", "Conflict",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<Map<String, Object>> handleUserCreationException(UserCreationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                )
        );
    }
}
