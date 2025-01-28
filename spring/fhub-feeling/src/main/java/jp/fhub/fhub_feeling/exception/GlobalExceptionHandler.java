package jp.fhub.fhub_feeling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import jp.fhub.fhub_feeling.exception.customexception.auth.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    // 422
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        logger.error("リクエストが無効です。入力内容をご確認ください。: ", ex);
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "リクエストが無効です。入力内容をご確認ください。");
    }

    // 会員登録
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("会員登録エラー: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // 権限
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("権限エラー: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    // ログイン
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(LoginException ex) {
        logger.error("ログインエラー: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // 401
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationCredentialsNotFoundException ex) {
        logger.error("認証に失敗しました。再度ログインしてください。: ", ex);
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "認証に失敗しました。再度ログインしてください。");
    }

    // 403
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(ResponseStatusException ex) {
        logger.error("このページにアクセスできません。詳細は管理者にお問い合わせください。: ", ex);
        return createErrorResponse(HttpStatus.FORBIDDEN, "このページにアクセスできません。詳細は管理者にお問い合わせください。");
    }

    // 500系
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        if (ex instanceof IllegalStateException) {
            logger.error("現在サービスを利用できません。時間をおいて再度お試しください。: ", ex);
            return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "現在サービスを利用できません。時間をおいて再度お試しください。");
        }
        logger.error("サーバーで問題が発生しました。時間をおいて再度お試しください。: ", ex);
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
