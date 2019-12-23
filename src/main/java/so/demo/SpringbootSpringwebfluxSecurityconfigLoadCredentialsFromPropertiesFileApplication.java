package so.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
public class SpringbootSpringwebfluxSecurityconfigLoadCredentialsFromPropertiesFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSpringwebfluxSecurityconfigLoadCredentialsFromPropertiesFileApplication.class, args);
    }
}

@RestController
class MyController{

    @GetMapping("/customer/hello")
    public String customer (Principal principal) {
        return "Hello " + principal.getName();
    }

}

@Configuration
@ConfigurationProperties(prefix = "secret")
class SecuredProperties {

    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    private SecuredProperties securedProperties;

    SecurityConfig(SecuredProperties securedProperties){
        this.securedProperties = securedProperties;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/customer/**").hasRole("INTERNAL_APP")
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(securedProperties.getUser())
                .password(securedProperties.getPassword())
                .roles("INTERNAL_APP")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
