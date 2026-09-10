package com.LegalMeterology.Online_Verification.Exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.LegalMeterology.Online_Verification.Responses.ErrorResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409 - Conflict
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.CONFLICT.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.CONFLICT.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    // 400 - Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    // 404 - User Not Found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.NOT_FOUND.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    // 401 - Authentication / JWT Error
    @ExceptionHandler({
            BadCredentialsException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    // 400 - Invalid JSON / Malformed Request Body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    // 400 - Illegal Argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .message(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .apiPath(request.getRequestURI())
                                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                AccessDeniedException ex,
                HttpServletRequest request) {

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.FORBIDDEN.value())
                            .apiPath(request.getRequestURI())
                            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }


        @ExceptionHandler({IllegalStateException.class, RuntimeException.class})
        public ResponseEntity<ErrorResponse> handleIllegalStateException(
                Exception ex,
                HttpServletRequest request) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                    ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .apiPath(request.getRequestURI())
                            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }

        @ExceptionHandler(VerificationException.class)
        public ResponseEntity<?> handleInstrumentNotVerified(
                VerificationException ex, HttpServletRequest request) {

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse.builder()
                            .message(ex.getMessage())
                            .status(HttpStatus.CONFLICT.value())
                            .apiPath(request.getRequestURI())
                            .error(HttpStatus.CONFLICT.getReasonPhrase())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
}
}

