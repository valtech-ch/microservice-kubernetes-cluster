package ch.valtech.kubernetes.microservice.cluster.authentication.repository;

import ch.valtech.kubernetes.microservice.cluster.authentication.model.UserRole;
import ch.valtech.kubernetes.microservice.cluster.authentication.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class LoadUsers {

    @Value("${data.users:admin,user}")
    private String[] users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers(UserRepository repo) {

        return args -> {
            for (int i = 0; i < users.length; i++) {
                String email = users[i] + "@" + users[i] + ".com";
                UserRole userRole = i >= 1 ? UserRole.ROLE_USER : UserRole.ROLE_ADMIN;
                String pwd = passwordEncoder.encode("pwd");
                repo.save(new User(null, email, pwd, userRole));
            }
        };
    }

}
