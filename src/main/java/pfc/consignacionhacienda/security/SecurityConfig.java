package pfc.consignacionhacienda.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.security.AuthProvider;
import java.util.Arrays;

@Profile("!test")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        logger.debug("Entra aqui");
        http.cors().and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET,"/api/test").hasAuthority("Rol")
            .antMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers(HttpMethod.POST,"/api/login").permitAll()
            .anyRequest().authenticated()
            .and()
                .addFilter(new MyAuthenticationFilter(authenticationManager()))//Filtro para los autenticar los login.
                .addFilter(new MyAuthorizationFilter(authenticationManager()))//Filtro para autorizar las demas peticiones
//            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                .csrf().disable();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
