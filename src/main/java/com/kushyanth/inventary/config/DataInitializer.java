package com.kushyanth.inventary.config;

import com.kushyanth.inventary.entity.Role;
import com.kushyanth.inventary.entity.User;
import com.kushyanth.inventary.repository.RoleRepository;
import com.kushyanth.inventary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        createRoleIfNotExists(Role.RoleName.ROLE_ADMIN);
        createRoleIfNotExists(Role.RoleName.ROLE_WAREHOUSE_MANAGER);
        createRoleIfNotExists(Role.RoleName.ROLE_VIEWER);

        // Create default admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User adminUser = new User("admin", "admin@inventory.com", passwordEncoder.encode("admin123"));
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setRoles(Set.of(adminRole));
            
            userRepository.save(adminUser);
            System.out.println("Default admin user created: username=admin, password=admin123");
        }

        // Create default warehouse manager if not exists
        if (!userRepository.existsByUsername("manager")) {
            Role managerRole = roleRepository.findByName(Role.RoleName.ROLE_WAREHOUSE_MANAGER)
                    .orElseThrow(() -> new RuntimeException("Manager role not found"));

            User managerUser = new User("manager", "manager@inventory.com", passwordEncoder.encode("manager123"));
            managerUser.setFirstName("Warehouse");
            managerUser.setLastName("Manager");
            managerUser.setRoles(Set.of(managerRole));
            
            userRepository.save(managerUser);
            System.out.println("Default manager user created: username=manager, password=manager123");
        }

        // Create default viewer if not exists
        if (!userRepository.existsByUsername("viewer")) {
            Role viewerRole = roleRepository.findByName(Role.RoleName.ROLE_VIEWER)
                    .orElseThrow(() -> new RuntimeException("Viewer role not found"));

            User viewerUser = new User("viewer", "viewer@inventory.com", passwordEncoder.encode("viewer123"));
            viewerUser.setFirstName("System");
            viewerUser.setLastName("Viewer");
            viewerUser.setRoles(Set.of(viewerRole));
            
            userRepository.save(viewerUser);
            System.out.println("Default viewer user created: username=viewer, password=viewer123");
        }
    }

    private void createRoleIfNotExists(Role.RoleName roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
            System.out.println("Role created: " + roleName);
        }
    }
}