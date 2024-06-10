package hexlet.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

@Component
public class WebSecurity {

    @Autowired
    private JwtDecoder jwtDecoder;

    public AuthorizationDecision checkUserId(Authentication authentication, RequestAuthorizationContext context) {
        boolean isCheckPassed;
        if (!authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        try {
            var authorizationHeader = context.getRequest().getHeader("Authorization");
            var token = authorizationHeader
                    .replaceFirst("Bearer", "")
                    .trim();
            var userId = jwtDecoder.decode(token).getClaim("userId");
            var pathId = Long.valueOf(context.getVariables().get("id"));
            isCheckPassed = userId.equals(pathId);
        } catch (Exception e) {
            isCheckPassed = false;
        }
        return new AuthorizationDecision(isCheckPassed);
    }
}
