package no.capraconsulting.config;

import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;

public class BasicAuthSecurityHandler {
    private BasicAuthSecurityHandler() {

    }

    public static SecurityHandler getBasicAuthSecurityHandler(String username, String password, String realm) {

        UserStore userStore = new UserStore();
        userStore.addUser(username, Credential.getCredential(password), new String[]{"user"});

        HashLoginService hashLoginService = new HashLoginService();
        hashLoginService.setUserStore(userStore);
        hashLoginService.setName(realm);

        ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
        constraintSecurityHandler.setAuthenticator(new BasicAuthenticator());
        constraintSecurityHandler.setLoginService(hashLoginService);
        constraintSecurityHandler.setRealmName(realm);

        constraintSecurityHandler.addConstraintMapping(getHealthConstraintMapping());
        constraintSecurityHandler.addConstraintMapping(getDefaultConstraintMapping());

        return constraintSecurityHandler;

    }

    private static ConstraintMapping getDefaultConstraintMapping() {
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setMethodOmissions(new String[]{HttpMethod.OPTIONS});
        constraintMapping.setPathSpec("/*");
        return constraintMapping;
    }

    private static ConstraintMapping getHealthConstraintMapping() {
        Constraint constraint = new Constraint();
        constraint.setAuthenticate(false);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setMethod("GET");
        constraintMapping.setPathSpec("/health/*");
        return constraintMapping;
    }
}
