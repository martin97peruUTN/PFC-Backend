package pfc.consignacionhacienda.rest.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/test")
public class TestRest {
    private static final Logger logger = LoggerFactory.getLogger(TestRest.class);
    @GetMapping
    public void testGet(){
        logger.debug("Llamada a GET test");
    }
}
