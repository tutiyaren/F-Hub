package jp.fhub.fhub_feeling.service;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.repository.UserRepository;

@Service
public class ValidationService {

    private final UserRepository userRepository;

    public ValidationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateRegistrationRequest(RegisterRequestDto request) {
        validatePasswordsMatch(request.getPassword(), request.getConfirmPassword());
        checkEmailNotRegistered(request.getEmail());
    }

    private void validatePasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("パスワードが一致していません");
        }
    }

    private void checkEmailNotRegistered(String email) {
        boolean isEmailAlreadyRegistered = userRepository.findByEmail(email).isPresent();
        if (isEmailAlreadyRegistered) {
            throw new IllegalArgumentException("既に登録されているメールアドレスです。別のメールアドレスでお試しください。");
        }
    }
    
    public String extractFirstErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
            .stream()
            .map(MessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse("入力値に誤りがあります");
    }
}
