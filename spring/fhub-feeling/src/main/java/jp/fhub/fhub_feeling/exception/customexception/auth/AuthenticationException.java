package jp.fhub.fhub_feeling.exception.customexception.auth;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
