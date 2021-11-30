package pfc.consignacionhacienda.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.JwtToken;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/user")
public class UserRest {
    private static final Logger logger = LoggerFactory.getLogger(UserRest.class);
    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<User> createUSer(@RequestBody User u){
        try {
            return ResponseEntity.ok(userService.saveUser(u));
        } catch (DuplicateUsernameException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
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
    
    @PatchMapping("/profile/{id}")
    public ResponseEntity<JwtToken> updateUserById(@PathVariable Integer id, @RequestBody Map<Object, Object> fields) {
        logger.debug(fields.keySet().toString());
        if (!fields.containsKey("rol")) {
            try {
                return ResponseEntity.ok(userService.updateUserProfileById(id, fields));
            } catch (UserNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (InvalidCredentialsException e){
                return ResponseEntity.badRequest().build();
            }catch (DuplicateUsernameException e) {
                logger.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

    @PatchMapping("/{id}/modificarpass")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePassword changePassword){
        if(changePassword == null || changePassword.getNewPassword() == null || changePassword.getNewPassword().isBlank() || changePassword.getOldPassword() == null ||changePassword.getOldPassword().isBlank() || changePassword.getNewPassword().equals(changePassword.getOldPassword())){
            return ResponseEntity.badRequest().build();
        }
        try {
            userService.changePasswordById(id, changePassword);
            LinkedHashMap<String, String> resultado = new LinkedHashMap<>();
            resultado.put("msg","Contraseña modificada correctamente");
            ObjectMapper objectMapper = new ObjectMapper();
            String res = objectMapper.writeValueAsString(resultado);
            return ResponseEntity.ok(res);
        }catch (UserNotFoundException e){
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (DuplicateUsernameException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }

    @PatchMapping("/admin-patch/{id}")
    public ResponseEntity<User> updateUserFromListById(@PathVariable Integer id, @RequestBody UserDTO userDTO){
        try {
            return ResponseEntity.ok(this.userService.updateUserById(id, userDTO));
        }catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch(DuplicateUsernameException | HttpForbidenException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<User>> getNotDeletedUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "") String name){
        try {
            return ResponseEntity.ok(userService.findUsersNotDeletedByName(page, limit, name));
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUserById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(userService.deleteUserById(id));
        } catch (DuplicateUsernameException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
