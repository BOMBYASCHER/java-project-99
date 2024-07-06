package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public String generateBearerToken(User user) {
        var userId = user.getId();
        var username = user.getUsername();
        var password = user.getPassword();
        var userFromRepository = userRepository.findById(userId).get();
        userFromRepository.setPassword(passwordEncoder.encode(password));
        userRepository.save(userFromRepository);
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return "Bearer " + jwtUtil.generateToken(username, userId).getTokenValue();
    }
    public String header() {
        return "Authorization";
    }
}
