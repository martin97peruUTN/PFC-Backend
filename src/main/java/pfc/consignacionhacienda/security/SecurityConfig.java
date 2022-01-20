package pfc.consignacionhacienda.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Profile("!test")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void configure( HttpSecurity http ) throws Exception {
        http.cors().and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET,"/api/test").hasAuthority("Rol")
            .antMatchers(HttpMethod.GET, "/api/locality/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers(HttpMethod.GET, "/api/category/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers(HttpMethod.GET, "/api/auction/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers(HttpMethod.GET, "/api/client/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers( "/api/locality/**").hasAnyAuthority("Administrador","Consignatario")
            .antMatchers( "/api/category/**").hasAnyAuthority("Administrador","Consignatario")
            .antMatchers( "/api/auction-user/history/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers( "/api/auction-user/assignment/**").hasAnyAuthority("Administrador", "Consignatario")
            .antMatchers( "/api/auction-user/users/**").hasAnyAuthority("Administrador", "Consignatario")
            .antMatchers( "/api/auction-user/own/**").authenticated()
            .antMatchers( "/api/auction-user/others/**").authenticated()
            .antMatchers( "/api/auction-user/**").hasAnyAuthority("Administrador")
            .antMatchers("/api/auction-batch/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers( "/api/auction/**").hasAnyAuthority("Administrador","Consignatario")
            .antMatchers("/api/user/profile/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
            .antMatchers("/api/user/user-list/**").hasAnyAuthority("Consignatario", "Administrador")
            .antMatchers("/api/user/**").hasAnyAuthority("Administrador")
            .antMatchers("/api/sold-batch/**").hasAnyAuthority("Administrador","Consignatario","Asistente")
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
