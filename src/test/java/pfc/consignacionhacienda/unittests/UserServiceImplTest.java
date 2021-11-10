package pfc.consignacionhacienda.unittests;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void initTest() throws DuplicateUsernameException {
        User u = userService.findUserById(1);
        //pass encoded: 1234
        u.setPassword("$2a$10$.K6U/unji7nI/Xvqfj5Z7efTBTN9/xbGuNj1n96d2ZCeANpJqR2uC");
        u.setName("testUser");
        u.setUsername("test");
        u.setRol("Administrador");
        userService.saveUser(u);
    }

    //Tests de cambio de password
    @Test
    void testChangePasswordSuccesfully(){
        String oldPassword = "1234";
        String newPassword = "nueva";
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        try {
            userService.changePasswordById(1, changePassword);
        } catch (DuplicateUsernameException e) {
            e.printStackTrace();
        }
        User u = userService.findUserById(1);
        System.out.println(u.getPassword());
        Assertions.assertTrue(passwordEncoder.matches(changePassword.getNewPassword(),u.getPassword()));
    }

    @Test
    void testChangePasswordWithSameOldPasswordAndNewPasswrod(){
        String oldPassword = "1234";
        String newPassword = "1234";
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        Exception e = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePasswordById(1, changePassword);
        });
        assertEquals("Las contraseñas deben ser distintas", e.getMessage());
    }
    @Test
    void testChangePasswordWithDifferentOldDBPasswordAndOldPassword(){
        String oldPassword = "aPass";
        String newPassword = "1234";
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        Exception e = assertThrows(InvalidCredentialsException.class, () -> {
            userService.changePasswordById(1, changePassword);
        });
        assertEquals("La contraseña antigua ingresada es diferente a su contraseña actual.", e.getMessage());
    }

    //Tests de modificacion de otros atributos del usuario.
    @Test
    void testChangeUserDataSuccesfully(){
        User userToEdit = userService.findUserById(1);
        assertEquals(userToEdit.getName(), "testUser");
        String nameEdited = "userEdited";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("name", nameEdited);
        try {
            userService.updateUserById(1, map);
        } catch (DuplicateUsernameException e) {
            e.printStackTrace();
        }
        User userEdited = userService.findUserById(1);
        assertEquals(userEdited.getName(), nameEdited);
    }

    @Test
    void testChangeUserDataInexistentUser(){
        User userToEdit = userService.findUserById(1);
        assertEquals(userToEdit.getName(), "testUser");
        String nameEdited = "userEdited";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("name", nameEdited);
        assertThrows(UserNotFoundException.class,()->{userService.updateUserById(2, map);});
    }

    @Test
    void testChangeUserInexistentAttribute(){
        User userToEdit = userService.findUserById(1);
        assertEquals(userToEdit.getName(), "testUser");
        String otherAttribute = "value";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("otroAtributo", otherAttribute);
        try {
            userService.updateUserById(1, map);
        } catch (DuplicateUsernameException e) {
            e.printStackTrace();
        }
        User userEdited = userService.findUserById(1);
        assertEquals(userToEdit.toString(),userEdited.toString());
    }
}
