package ozone.com.eproz.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ozone.com.eproz.model.RoleModel;
import ozone.com.eproz.model.UserModel;
import ozone.com.eproz.model.UserRoleModel;
import ozone.com.eproz.repository.RoleRepository;
import ozone.com.eproz.repository.UserRepository;

/**
 * Solves the chicken-and-egg problem: only an admin can create users, so who
 * creates the first admin?
 *
 * On startup, ensure the bootstrap admin uses ADMIN_PASSWORD and has ROLE_ADMIN.
 * A password in a Flyway migration would be in git forever; an environment variable is not.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    @Value("${pis.security.admin-password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RoleModel adminRole = roles.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN was not seeded by Flyway"));
        boolean created = users.findByUsername("admin").isEmpty();
        UserModel admin = users.findByUsername("admin")
                .orElseGet(() -> new UserModel("admin", "admin@localhost", passwordEncoder.encode(adminPassword)));
        boolean changed = created;

        if (!passwordEncoder.matches(adminPassword, admin.getPasswordHash())) {
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            changed = true;
        }
        if (admin.getUserRoles().stream()
                .noneMatch(userRole -> adminRole.getName().equals(userRole.getRole().getName()))) {
            admin.getUserRoles().add(new UserRoleModel(admin, adminRole));
            changed = true;
        }

        if (changed) {
            users.save(admin);
            log.warn("Created or synchronized the bootstrap 'admin' account from ADMIN_PASSWORD.");
        }
    }
}