package hexlet.code.controller;

import hexlet.code.dto.AuthenticationDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    String login(@RequestBody AuthenticationDTO authenticationDTO) {
        var email = authenticationDTO.getUsername();
        var password = authenticationDTO.getPassword();
        var authentication = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(authentication);
        var jwt = jwtUtil.generateToken(email);
        return jwt.getTokenValue();
    }
}
