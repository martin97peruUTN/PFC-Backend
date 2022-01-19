package pfc.consignacionhacienda.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;
import pfc.consignacionhacienda.utils.ErrorResponse;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/user")
public class UserRest {
    private static final Logger logger = LoggerFactory.getLogger(UserRest.class);
    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<?> createUSer(@RequestBody User u){
        try {
            return ResponseEntity.ok(userService.saveUser(u));
        } catch (DuplicateUsernameException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (BadHttpRequest badHttpRequest) {
            badHttpRequest.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(badHttpRequest.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id){
        try{
            logger.debug(userService.findUserById(id).toString());
            return ResponseEntity.ok(userService.findUserById(id));
        }catch (UserNotFoundException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PatchMapping("/profile/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Integer id, @RequestBody Map<Object, Object> fields) {
        logger.debug(fields.keySet().toString());
        if (!fields.containsKey("rol")) {
            try {
                return ResponseEntity.ok(userService.updateUserProfileById(id, fields));
            } catch (UserNotFoundException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
            } catch (BadHttpRequest e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
            } catch (InvalidCredentialsException e){
                logger.error(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
            } catch (DuplicateUsernameException e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
            }catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            logger.error("El usuario a actualizar no debe traer rol.");
            return new ResponseEntity<>(new ErrorResponse("El usuario a actualizar no debe traer rol."), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/profile/{id}/modificarpass")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePassword changePassword){
        if(changePassword == null || changePassword.getNewPassword() == null || changePassword.getNewPassword().isBlank() || changePassword.getOldPassword() == null ||changePassword.getOldPassword().isBlank() || changePassword.getNewPassword().equals(changePassword.getOldPassword())){
            return new ResponseEntity<>(new ErrorResponse("Parámetros inválidos"), HttpStatus.BAD_REQUEST);
        }
        try {
            userService.changePasswordById(id, changePassword);
            LinkedHashMap<String, String> resultado = new LinkedHashMap<>();
            resultado.put("msg","Contraseña modificada correctamente");
            ObjectMapper objectMapper = new ObjectMapper();
            String res = objectMapper.writeValueAsString(resultado);
            return ResponseEntity.ok(res);
        } catch (UserNotFoundException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (DuplicateUsernameException | HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/admin-patch/{id}")
    public ResponseEntity<?> updateUserFromListById(@PathVariable Integer id, @RequestBody UserDTO userDTO){
        try {
            return ResponseEntity.ok(this.userService.updateUserById(id, userDTO));
        } catch (UserNotFoundException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InvalidCredentialsException | BadHttpRequest e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch(DuplicateUsernameException | HttpForbidenException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-list")
    public ResponseEntity<?> getNotDeletedUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "") String name){
        try {
            return ResponseEntity.ok(userService.findUsersNotDeletedByName(page, limit, name));
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-list/{id}")
    public ResponseEntity<?> getUsersByNameExceptIdAndNotAdmin(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "") String name){
        try {
            return ResponseEntity.ok(userService.findUsersByNameExceptIdAndNotAdmin(name,id));
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(userService.deleteUserById(id));
        } catch (DuplicateUsernameException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (BadHttpRequest badHttpRequest) {
            badHttpRequest.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(badHttpRequest.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
