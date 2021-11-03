package pfc.consignacionhacienda.exceptions.user;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundException extends AuthenticationException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
