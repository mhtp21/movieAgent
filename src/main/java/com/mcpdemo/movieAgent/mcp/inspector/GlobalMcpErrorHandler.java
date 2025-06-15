package com.mcpdemo.movieAgent.mcp.inspector;

import com.mcpdemo.movieAgent.mcp.protocol.dto.common.ApiError;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalMcpErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalMcpErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, ServerWebExchange exchange) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).toList();

        String path = exchange.getRequest().getPath().value();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "MCP Protocol Validation Failed", path, LocalDateTime.now(), errors);
        log.warn("MCP Protocol contract violated. Path: {}, Errors: {}", path, errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected internal server error occurred.", path, LocalDateTime.now(), null);
        log.error("Unhandled exception caught by MCP Inspector. Path: {}", path, ex);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
