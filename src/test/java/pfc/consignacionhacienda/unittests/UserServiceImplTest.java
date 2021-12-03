package pfc.consignacionhacienda.unittests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import pfc.consignacionhacienda.dao.UserDAO;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.exceptions.user.UserNotFoundException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)//Esto porque sino se quedaba guardado el comportamiento de los mocks y en cada metodo necesito comportamientos distintos
public class UserServiceImplTest {

//    @Autowired
//    private UserService userService;
//    private UserService userServiceMock;
//
//    @Autowired
//    private UserDAO userDAO;
//    private UserDAO userDAOMock;

    @SpyBean
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @SpyBean
    private UserDAO userDAOMock;
    private User user;
    @BeforeEach
    public void initTest() throws DuplicateUsernameException {
        User u = userService.findUserById(1);
        //pass encoded: 1234
        System.out.println(u.getId());
        u.setPassword("$2a$10$.K6U/unji7nI/Xvqfj5Z7efTBTN9/xbGuNj1n96d2ZCeANpJqR2uC");
        u.setName("testUser");
        u.setUsername("test");
        u.setRol("Administrador");
        try {
            userService.saveUser(u);
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        Mockito.doReturn(u).when(userService).getCurrentUser();

        user = new User();
        //pass encoded: 1234
        user.setPassword("$2a$10$.K6U/unji7nI/Xvqfj5Z7efTBTN9/xbGuNj1n96d2ZCeANpJqR2uC");
        user.setName("testUser");
        user.setUsername("test");
        user.setRol("Administrador");
    }

    //Tests de cambio de password
    @Test
    void testChangePasswordSuccesfully(){
        String oldPassword = "1234";
        String newPassword = "nueva";
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        try {
            userService.changePasswordById(1, changePassword);
        } catch (DuplicateUsernameException | HttpForbidenException e) {
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
            userService.updateUserProfileById(1, map);
        } catch (DuplicateUsernameException | BadHttpRequest e) {
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
//        Map<Object, Object> map = new LinkedHashMap<>();
//        map.put("name", nameEdited);
        UserDTO userDTO = new UserDTO();
        userDTO.setName(nameEdited);
        assertThrows(UserNotFoundException.class,()->{userService.updateUserById(2, userDTO);});
    }

    @Test
    void testChangeUserInexistentAttribute(){
        User userToEdit = userService.findUserById(1);
        assertEquals(userToEdit.getName(), "testUser");
        String otherAttribute = "value";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("otroAtributo", otherAttribute);
        try {
            userService.updateUserProfileById(1, map);
        } catch (DuplicateUsernameException | BadHttpRequest e) {
            e.printStackTrace();
        }
        User userEdited = userService.findUserById(1);
        assertEquals(userToEdit.toString(),userEdited.toString());
    }

    //Tests historia: CRUD usuarios.

    //deleteUserById
    @Test
    void testDeleteUserByIdSuccessfully() {
        User userDeleted = user;
        userDeleted.setDeleted(true);
        Mockito.doReturn(userDeleted).when(userDAOMock).save(any(User.class));
        try {
            user = userService.deleteUserById(1);
            Mockito.doReturn(Optional.of(user)).when(userDAOMock).findById(any(Integer.class));
            assertEquals(user.isDeleted(),true);
        } catch (DuplicateUsernameException e) {
            e.printStackTrace();
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
    }
    //deleteUserById inexistent user
    @Test
    void testDeleteInexistentUserById() {
        User userDeleted = user;
        userDeleted.setDeleted(true);
        Mockito.doReturn(userDeleted).when(userDAOMock).save(any(User.class));
        Mockito.doReturn(Optional.empty()).when(userDAOMock).findById(any(Integer.class));
        assertThrows(UserNotFoundException.class, ()->{userService.deleteUserById(1);});
    }

    //saveUser
    @Test
    void testSaveUserSuccesfully() {
        user.setUsername("nuevo");
        AtomicReference<User> userToSave = new AtomicReference<>(user);
        user.setId(2);
        Mockito.doReturn(user).when(userDAOMock).save(any(User.class));
        assertDoesNotThrow(()-> userToSave.set(userService.saveUser(userToSave.get())));
        assertEquals(userToSave.get().getId(), user.getId());
    }

    //saveUser
    @Test
    void testSaveUserWithSameUsernameInDB() {
        user.setUsername("nuevo");
        User userToSave = user;
        Mockito.doReturn(Optional.of(user)).when(userDAOMock).findByUsername(any(String.class));
        assertThrows(DuplicateUsernameException.class, () -> userService.saveUser(userToSave));
    }

    //updateUser
    @Test
    void testUpdateUserSuccessfully() {
        UserDTO changes = new UserDTO();
        changes.setName("NombreEditado");
        changes.setLastname("ApellidoEditad");
        changes.setUsername("usernameEditado");
        changes.setPassword("newPassword");
        Mockito.doReturn(Optional.of(user)).when(userDAOMock).findById(any(Integer.class));
        assertNotEquals(user.getName(), changes.getName());
        user.setName(changes.getName());
        user.setLastname(changes.getLastname());
        user.setUsername(changes.getUsername());
        user.setPassword(changes.getPassword());
        Mockito.doReturn(user).when(userDAOMock).save(any(User.class));
        User uModified=null;
        try {
            uModified = userService.updateUserById(1, changes);
        } catch (DuplicateUsernameException e) {
            e.printStackTrace();
        } catch (BadHttpRequest e) {
            e.printStackTrace();
        }
        assertEquals(changes.getName(), uModified.getName());
        assertEquals(changes.getLastname(), uModified.getLastname());
        assertEquals(changes.getUsername(), uModified.getUsername());
//        assertEquals(changes.getPassword(), uModified.getPassword());
    }

    //updateUser
    @Test
    void testUpdateUserWithExistentUsername() {
        UserDTO changes = new UserDTO();
        changes.setName("test");
        Mockito.doReturn(Optional.of(user)).when(userDAOMock).findByUsername(any(String.class));
        assertThrows(DuplicateUsernameException.class, ()->userService.updateUserById(1,changes));
    }

    //updateUser
    @Test
    void testUpdateUserRol() {
        UserDTO changes = new UserDTO();
        changes.setRol("Asistente");
        Mockito.doReturn(Optional.empty()).when(userDAOMock).findByUsername(any(String.class));
        assertThrows(BadHttpRequest.class, ()->userService.updateUserById(1,changes));
    }

    //updateUser
    @Test
    void testUpdateInexistentUser() {
        UserDTO changes = new UserDTO();
        changes.setName("Name");
        Mockito.doReturn(Optional.empty()).when(userDAOMock).findByUsername(any(String.class));
        Mockito.doReturn(Optional.empty()).when(userDAOMock).findById(any(Integer.class));
        assertThrows(UserNotFoundException.class, ()->userService.updateUserById(1,changes));
    }

    //updateUser
    @Test
    void testUpdateUserWithSamePassword() {
        UserDTO changes = new UserDTO();
        changes.setPassword("1234");
        Mockito.doReturn(Optional.empty()).when(userDAOMock).findByUsername(any(String.class));
//        Mockito.doReturn(Optional.empty()).when(userDAOMock).findById(any(Integer.class));
        assertThrows(InvalidCredentialsException.class, ()->userService.updateUserById(1,changes));
    }
}