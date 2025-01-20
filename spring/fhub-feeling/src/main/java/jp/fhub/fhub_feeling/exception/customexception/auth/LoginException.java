package jp.fhub.fhub_feeling.exception.customexception.auth;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}
