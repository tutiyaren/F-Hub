package jp.fhub.fhub_feeling.service;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ValidationService {
    
    public String extractFirstErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
            .stream()
            .map(MessageSourceResolvable::getDefaultMessage)
            .findFirst()
            .orElse("入力値に誤りがあります");
    }
}
