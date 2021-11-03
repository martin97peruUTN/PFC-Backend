package pfc.consignacionhacienda.rest.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LogInRest {

    private static final Logger logger = LoggerFactory.getLogger(LogInRest.class);

    @PostMapping
    public void logIn(){
        logger.debug("Llamada a POST login");
    }

}
