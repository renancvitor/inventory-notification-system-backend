package com.github.renancvitor.inventory.exception.handler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.renancvitor.inventory.exception.model.ApiError;
import com.github.renancvitor.inventory.exception.model.DataValidationError;
import com.github.renancvitor.inventory.exception.types.auth.AuthorizationException;
import com.github.renancvitor.inventory.exception.types.common.EntityNotFoundException;
import com.github.renancvitor.inventory.exception.types.common.JsonSerializationException;
import com.github.renancvitor.inventory.exception.types.common.ValidationException;
import com.github.renancvitor.inventory.exception.types.product.DuplicateProductException;
import com.github.renancvitor.inventory.infra.messaging.errorlog.ErrorLogPublisherService;
import com.github.renancvitor.inventory.util.StackTraceUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorHandler {

    private final ErrorLogPublisherService errorLogPublisherService;

    public ErrorHandler(ErrorLogPublisherService errorLogPublisherService) {
        this.errorLogPublisherService = errorLogPublisherService;
    }

    private void logError(Exception ex, HttpServletRequest request) {
        errorLogPublisherService.publish(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                StackTraceUtils.formatStackTrace(ex, 10),
                request.getRequestURI());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiError> handle403(AuthorizationException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ApiError> handle409(DuplicateProductException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handle404(EntityNotFoundException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        logError(ex, request);

        List<DataValidationError> validationErrors = ex.getFieldErrors().stream()
                .map(DataValidationError::new)
                .toList();

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Erro de validação nos campos.", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(BadCredentialsException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Acesso negado.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Requisição com JSON mal formatado.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(JsonSerializationException.class)
    public ResponseEntity<ApiError> handleJsonSerializationError(JsonSerializationException ex,
            HttpServletRequest request) {
        logError(ex, request);

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

}
