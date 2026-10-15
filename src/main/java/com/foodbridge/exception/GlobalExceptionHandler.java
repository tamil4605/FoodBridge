package com.foodbridge.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String requestedWith = request.getHeader("X-Requested-With");
        return (uri != null && uri.startsWith("/api/")) ||
               (accept != null && accept.contains("application/json")) ||
               "XMLHttpRequest".equalsIgnoreCase(requestedWith);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", 404);
            body.put("error", "Not Found");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }
        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleForbidden(UnauthorizedException ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", 403);
            body.put("error", "Forbidden");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }
        model.addAttribute("status", 403);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBadRequest(BadRequestException ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", 400);
            body.put("error", "Bad Request");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        model.addAttribute("status", 400);
        model.addAttribute("error", "Bad Request");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        response.put("details", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGenericException(Exception ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", 500);
            body.put("error", "Internal Server Error");
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred. Please try again.");
        return "error/500";
    }
}
