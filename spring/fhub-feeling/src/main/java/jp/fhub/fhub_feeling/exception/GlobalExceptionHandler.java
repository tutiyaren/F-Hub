package jp.fhub.fhub_feeling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationCredentialsNotFoundException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "認証に失敗しました。再度ログインしてください。");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(ResponseStatusException ex) {
        if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
            return createErrorResponse(HttpStatus.FORBIDDEN, "このページにアクセスできません。詳細は管理者にお問い合わせください。");
        }
        return createErrorResponse(HttpStatus.NOT_FOUND, "お探しのページが見つかりません。URLが正しいかご確認ください。");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(BindException ex) {
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "リクエストが無効です。入力内容をご確認ください。");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        if (ex instanceof IllegalStateException) {
            return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "現在サービスを利用できません。時間をおいて再度お試しください。");
        }
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "サーバーで問題が発生しました。時間をおいて再度お試しください。");
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return new ResponseEntity<>(errorResponse, status);
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorResponse {
        private final int result;
        private final String message;
    }
}
