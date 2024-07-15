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
        Long userId = user.getId();
        String username = user.getUsername();
        String password = user.getPassword();
        User userFromRepository = userRepository.findById(userId).get();
        String encodedPassword = passwordEncoder.encode(password);
        userFromRepository.setPassword(encodedPassword);
        userRepository.save(userFromRepository);
        user.setPassword(encodedPassword);
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return "Bearer " + jwtUtil.generateToken(username).getTokenValue();
    }
    public String header() {
        return "Authorization";
    }
}
