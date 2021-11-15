package pfc.consignacionhacienda.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pfc.consignacionhacienda.model.User;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;

//Esta clase es utilizada por spring para mantener la informacion de usuario.
//Se podria usar para tambien obtener los roles, sin embargo, los obtenemos desde UsernamePasswordAuthenticationToken
public class Principal implements UserDetails {

    private String name;
    private String lastname;
    private String username;
    private String password;
    private Integer id;

    public Principal (User user){
        name = user.getName();
        lastname = user.getLastname();
        username = user.getUsername();
        password = user.getPassword();
        id = user.getId();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public Integer getId() {
        return id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
