package pfc.consignacionhacienda.exceptions.user;

import org.springframework.security.core.AuthenticationException;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(String s) {
        super(s);
    }
}
