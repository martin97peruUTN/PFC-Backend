package pfc.consignacionhacienda.integrationtests;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
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

    @Autowired
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
        authorities.add(new SimpleGrantedAuthority("Rol"));
//        when(authenticationMock.getAuthorities()).thenReturn((List)authorities);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getAuthorities()).thenReturn((List)authorities);
    }

    //Tests de modificacion de password usuario.
    @Test
    void testChangePasswordSuccesfully(){
        String server = "http://localhost:" + puerto + "/api/user/1/modificarpass";
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
        String server = "http://localhost:" + puerto + "/api/user/1/modificarpass";
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
        String server = "http://localhost:" + puerto + "/api/user/1/modificarpass";
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

    @Test
    void testChangePasswordInexistentUser(){
        String server = "http://localhost:" + puerto + "/api/user/100/modificarpass";
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
        String server = "http://localhost:" + puerto + "/api/user/1";
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
        String server = "http://localhost:" + puerto + "/api/user/2";
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
        String server = "http://localhost:" + puerto + "/api/user/1";
        String rolEdited = "OtroRol";
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put("rol", rolEdited);
        testRestTemplatePatch.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
        HttpEntity<Map<Object,Object>> requestChangeUserData = new HttpEntity<>(map);

        ResponseEntity<String> response = testRestTemplate.exchange(server, HttpMethod.PATCH, requestChangeUserData,
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

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
        String server = "http://localhost:" + puerto + "/api/user/1";
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
}
