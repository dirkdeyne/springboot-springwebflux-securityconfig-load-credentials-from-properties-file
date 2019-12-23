package so.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@EnableWebFluxSecurity
class SecurityConfig {

    @Autowired private Environment environment;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        System.out.println("http");
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
        System.out.println("userDetailsService");
        String hashpw = BCrypt.hashpw("password", BCrypt.gensalt());
        System.out.println(hashpw);
        UserDetails user = User.builder()
                .username(environment.getProperty("secret.user"))
                .password(environment.getProperty("secret.password"))
                .roles("INTERNAL_APP")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
}
