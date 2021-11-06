package pfc.consignacionhacienda.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder {
    //TODO ver como se implementar esto como inyeccion de dependencia.
    @Autowired
    private static PasswordEncoder pwEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    public static  PasswordEncoder getPasswordEncoder(){
        return pwEncoder;
    }
}
