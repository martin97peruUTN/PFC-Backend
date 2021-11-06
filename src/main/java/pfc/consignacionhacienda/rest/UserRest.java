package pfc.consignacionhacienda.rest;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/user")
public class UserRest {
    private static final Logger logger = LoggerFactory.getLogger(UserRest.class);
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id){

        try{
            logger.debug(userService.findUserById(id).toString());
            return ResponseEntity.ok(userService.findUserById(id));
        }catch (UserNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<JwtToken> updateUserById(@PathVariable Integer id, @RequestBody Map<Object, Object> fields) {
        logger.debug(fields.keySet().toString());
        if (!fields.containsKey("rol")) {
            try {
                return ResponseEntity.ok(userService.updateUserById(id, fields));
            } catch (UserNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (InvalidCredentialsException e){
                return ResponseEntity.badRequest().build();
            }catch (Exception e) {
//                logger.error(e.printStackTrace());
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
        else{
            logger.error("El usuario a actualizar no debe traer rol.");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/modificarpass")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePassword changePassword){
        if(changePassword == null || changePassword.getNewPassword() == null || changePassword.getNewPassword().isBlank() || changePassword.getOldPassword() == null ||changePassword.getOldPassword().isBlank() || changePassword.getNewPassword().equals(changePassword.getOldPassword())){
            return ResponseEntity.badRequest().build();
        }
        try {
            userService.changePasswordById(id, changePassword);
            return ResponseEntity.ok("Contraseña modificada correctamente");
        }catch (UserNotFoundException e){
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
}
