package org.example.filexpressbackend;


import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.Role;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.RoleRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
// Lombok will generate a constructor with all the required fields, for autowiring
@RequiredArgsConstructor
// command line runner interface is used to run the code when the application starts
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // check if the database is empty
        if (userRepository.count() > 0) {
            return;
        }
        // else, populate the database with some data,create roles, admin and user, and save them to the database
        Role adminRole = new Role();
        adminRole.setRoleName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName("ROLE_USER");
        roleRepository.save(userRole);

        // create an admin user with an admin role and save it to the database
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setDateOfBirth(null);
        // encode the password
        adminUser.setPassword(passwordEncoder.encode("admin"));
        List<Role> roles = new ArrayList<>();
        roles.add(adminRole);
        adminUser.setRoles(roles);
        userRepository.save(adminUser);

        // create a user with a user role and save it to the database
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@gmail.com");
        user.setDateOfBirth(null);
        // encode the password
        user.setPassword(passwordEncoder.encode("user"));
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(userRole);
        user.setRoles(userRoles);
        userRepository.save(user);
    }
}