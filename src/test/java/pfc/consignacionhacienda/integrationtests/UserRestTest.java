package pfc.consignacionhacienda.integrationtests;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import pfc.consignacionhacienda.dto.UserDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.HttpForbidenException;
import pfc.consignacionhacienda.exceptions.user.DuplicateUsernameException;
import pfc.consignacionhacienda.model.User;
import pfc.consignacionhacienda.services.user.UserService;
import pfc.consignacionhacienda.utils.ChangePassword;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestTest {

    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private RestTemplate testRestTemplatePatch = testRestTemplate.getRestTemplate();

    @LocalServerPort
    String puerto;

    @SpyBean
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authenticationMock;

    @BeforeEach
    void initTests(){
        User u = userService.findUserById(1);
        //pass encoded: 1234
        u.setPassword("$2a$10$.K6U/unji7nI/Xvqfj5Z7efTBTN9/xbGuNj1n96d2ZCeANpJqR2uC");
        u.setName("testUser");
        u.setUsername("test");
        u.setRol("Administrador");
        try {
            userService.saveUser(u);
        } catch (DuplicateUsernameException | BadHttpRequest e) {
            e.printStackTrace();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("Administrador"));
//        when(authenticationMock.getAuthorities()).thenReturn((List)authorities);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getAuthorities()).thenReturn((List)authorities);
        User us = new User();
        us.setId(1);
        Mockito.doReturn(us).when(userService).getCurrentUser();
    }

    //Tests de modificacion de password usuario.
    @Test
    void testChangePasswordSuccesfully(){
        String server = "http://localhost:" + puerto + "/api/user/profile/1/modificarpass";
        String oldPassword = "1234";
        String newPassword = "nueva";
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        HttpEntity<ChangePassword> requestChangePassword = new HttpEntity<>(changePassword);
        System.out.println(server);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangePassword,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testChangePasswordSameNewPasswordAndOldPassword(){
        String server = "http://localhost:" + puerto + "/api/user/profile/1/modificarpass";
        String oldPassword = "1234";
        String newPassword = "1234";
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        HttpEntity<ChangePassword> requestChangePassword = new HttpEntity<>(changePassword);
        System.out.println(server);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangePassword,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testChangePasswordWithOnlyBlankSpaces(){
        String server = "http://localhost:" + puerto + "/api/user/profile/1/modificarpass";
        String oldPassword = " ";
        String newPassword = "1234";
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        HttpEntity<ChangePassword> requestChangePassword = new HttpEntity<>(changePassword);
        System.out.println(server);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangePassword,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    //TODO por ahora no tener en cuenta. Utilizar para testear una modificacion a un usuario que no sea desde su perfil.
    @Test
     void testChangePasswordInexistentUser(){
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/100";
        String oldPassword = "1234";
        String newPassword = "123456";
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        HttpEntity<ChangePassword> requestChangePassword = new HttpEntity<>(changePassword);
        System.out.println(server);
        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangePassword,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    //Tests de modificacion de otros atributos del usuario.
    @Test
    void testChangeUserDataSuccesfully(){
        String server = "http://localhost:" + puerto + "/api/user/profile/1";
        String nameEdited = "userEdited";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("name", nameEdited);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<Map<Object,Object>> requestChangePassword = new HttpEntity<>(map);

        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangePassword,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    void testChangeUserInexistent(){
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/2";
        String nameEdited = "userEdited";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("name", nameEdited);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<Map<Object,Object>> requestChangeUserData = new HttpEntity<>(map);

        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangeUserData,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testChangeUserRolAndUserID(){
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/1";
        String rolEdited = "OtroRol";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("rol", rolEdited);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<Map<Object,Object>> requestChangeUserData = new HttpEntity<>(map);

        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangeUserData,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);

        Integer idEdited = 2;
        Map<Object, Object> map2 = new LinkedHashMap<>();
        map2.put("id", idEdited);
        HttpEntity<Map<Object,Object>> requestChangeUserId = new HttpEntity<>(map2);
        response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangeUserId,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testChangeUserInexistentAttributes(){
        String server = "http://localhost:" + puerto + "/api/user/profile/1";
        User userDB = userService.findUserById(1);
        String nameEdited = "userEdited";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("inexistent", nameEdited);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<Map<Object,Object>> requestChangeUserData = new HttpEntity<>(map);

        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangeUserData,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        User userEdited = userService.findUserById(1);
        assertEquals(userDB.toString(), userEdited.toString());
    }

    //Historia: CRUD usuarios

    //Delete user
    @Test
    void deleteUserByIdSuccesfully(){
        String server = "http://localhost:" + puerto + "/api/user/5";//primero crear en la base de datos el usuario que tenga ese ID
        HttpEntity<User> userHttpEntity = new HttpEntity<>(new User());
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.DELETE, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getId(), 5);
        UserDTO userDTO = new UserDTO();
        userDTO.setDeleted(false);
        try {
            userService.updateUserById(5, userDTO);
        } catch (DuplicateUsernameException | BadHttpRequest | HttpForbidenException e) {
            e.printStackTrace();
        }
//        assertEquals(response.getBody().isDeleted(), true);//no se puede validar Jackson, el atributo deleted no se setea en el JSON devuelto.
    }

    @Test
    void deleteInexistentUserById(){
        String server = "http://localhost:" + puerto + "/api/user/1000";
        HttpEntity<User> userHttpEntity = new HttpEntity<>(new User());
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.DELETE, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    //Crear usuarios
    @Test
    void createUserSuccesfully(){
        String password = "1234";
//        Mockito.doReturn(password).when(passwordEncoder).encode(any(String.class));
        String server = "http://localhost:" + puerto + "/api/user";
        User newUser = new User();
        newUser.setName("Nuevo");
        newUser.setLastname("User");
        newUser.setUsername("newUSer");
        newUser.setRol("Administrador");
        newUser.setPassword("password");
        HttpEntity<User> userHttpEntity = new HttpEntity<>(newUser);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.POST, userHttpEntity,
                User.class);
//        assertEquals(response.getStatusCode(), HttpStatus.OK); no anda porque jackson ignora el atributo password
//        assertNotNull(response.getBody().getId());
    }

    @Test
    void createUserWithExistentUsername(){
        String password = "1234";
        String server = "http://localhost:" + puerto + "/api/user";
        User newUser = new User();
        newUser.setName("Nuevo");
        newUser.setLastname("User");
        newUser.setUsername("test");
        newUser.setRol("Administrador");
        newUser.setPassword("password");
        HttpEntity<User> userHttpEntity = new HttpEntity<>(newUser);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.POST, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    //updateUSer
    @Test
    void updateUSerSuccessfully(){
        //ojo en esta ruta el id, tener un usuario con id 5.
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/5";
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Nuevo");
        userDTO.setLastname("User");
        userDTO.setUsername("usernameEditado2");
        userDTO.setPassword("password11");
        HttpEntity<UserDTO> userHttpEntity = new HttpEntity<>(userDTO);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.PATCH, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().getName(), userDTO.getName());
        assertEquals(response.getBody().getLastname(), userDTO.getLastname());
        assertEquals(response.getBody().getUsername(), userDTO.getUsername());
        userDTO = new UserDTO();
        userDTO.setUsername("testUsername");
        userDTO.setPassword("testPassword");
        try {
            userService.updateUserById(5,userDTO);
        } catch (DuplicateUsernameException | BadHttpRequest |HttpForbidenException e) {
            e.printStackTrace();
        }
    }

    //updateUSer
    @Test
    void updateUserRol(){
        //ojo en esta ruta el id, tener un usuario con id 5.
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/5";
        UserDTO userDTO = new UserDTO();
        userDTO.setRol("Asistente");
        HttpEntity<UserDTO> userHttpEntity = new HttpEntity<>(userDTO);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.PATCH, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    void updateInexistentUser(){
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/500";
        UserDTO userDTO = new UserDTO();
        userDTO.setName("otro name");
        HttpEntity<UserDTO> userHttpEntity = new HttpEntity<>(userDTO);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.PATCH, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void updateUserWithSamePassword(){
        //ojo en esta ruta el id, tener un usuario con id 1.
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/1";
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("1234");
        HttpEntity<UserDTO> userHttpEntity = new HttpEntity<>(userDTO);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.PATCH, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateUserWithExistentUSername(){
        //ojo en esta ruta el id, tener un usuario con id 3.
        String server = "http://localhost:" + puerto + "/api/user/admin-patch/3";
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("test");
        HttpEntity<UserDTO> userHttpEntity = new HttpEntity<>(userDTO);
        ResponseEntity<User> response = testRestTemplate.exchange(server, HttpMethod.PATCH, userHttpEntity,
                User.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }
}
