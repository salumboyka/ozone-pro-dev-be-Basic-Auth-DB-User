package ozone.com.eproz.service;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import ozone.com.eproz.config.AdminAccountInitializer;
import ozone.com.eproz.dto.UserDtoRequest;
import ozone.com.eproz.dto.UserDtoResponse;
import ozone.com.eproz.repository.RoleRepository;
import ozone.com.eproz.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserServicesTest {

    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService authenticationUsers;

    @Autowired
    private AdminAccountInitializer adminAccountInitializer;

    @Value("${pis.security.admin-password}")
    private String configuredAdminPassword;

    @Test
    void registerHashesPasswordAndAssignsRequestedRole() {
        String username = "test-" + UUID.randomUUID();
        String email = username + "@example.com";
        String password = "correct-horse-battery";
        var role = roleRepository.findAll().get(0);
        UserDtoRequest request = new UserDtoRequest(username, email, password, role.getId());

        assertThat(request.toString()).doesNotContain(password);

        UserDtoResponse response = userServices.register(request);

        assertThat(response.roles()).containsExactly(role.getName());

        var persistedUser = userRepository.findById(response.id()).orElseThrow();
        assertThat(persistedUser.getPasswordHash()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, persistedUser.getPasswordHash())).isTrue();
    }

    @Test
    void authenticatesRegisteredDatabaseUserWithStoredRole() {
        String username = "admin-" + UUID.randomUUID();
        String email = username + "@x.test";
        String password = "correct-horse-battery";
        var adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
        UserDtoRequest request = new UserDtoRequest(username, email, password, adminRole.getId());
        userServices.register(request);

        var user = authenticationUsers.loadUserByUsername(username);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void synchronizesExistingAdminPasswordFromConfiguration() {
        adminAccountInitializer.run(new DefaultApplicationArguments(new String[0]));

        var admin = userRepository.findByUsername("admin").orElseThrow();
        assertThat(passwordEncoder.matches(configuredAdminPassword, admin.getPasswordHash())).isTrue();
        assertThat(admin.getUserRoles()).anyMatch(userRole -> userRole.getRole().getName().equals("ROLE_ADMIN"));
    }
}