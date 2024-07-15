package hexlet.code.config;

import hexlet.code.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSecurity {

    @Autowired
    private UserRepository userRepository;

    public AuthorizationDecision checkUserId(Authentication authentication, RequestAuthorizationContext context) {
        boolean isCheckPassed;
        if (!authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        try {
            var email = ((Jwt) authentication.getPrincipal()).getSubject();
            var pathId = Long.valueOf(context.getVariables().get("id"));
            var user = userRepository.findByEmail(email);
            isCheckPassed = user.map(u -> u.getId().equals(pathId)).orElse(false);
        } catch (Exception e) {
            isCheckPassed = false;
        }
        return new AuthorizationDecision(isCheckPassed);
    }
}
