package jp.fhub.fhub_feeling.controller;

import jp.fhub.fhub_feeling.dto.requestDto.UserRequestDto;
import jp.fhub.fhub_feeling.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequestDto userRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userRequestDto.getEmail(),
                            userRequestDto.getPassword()));
            String jwt = jwtUtil.generateToken(userRequestDto.getEmail());
            return ResponseEntity.status(201).body(jwt);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}
