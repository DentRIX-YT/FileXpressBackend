package org.example.filexpressbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.config.JwtUtil;
import org.example.filexpressbackend.dto.UserDto;
import org.example.filexpressbackend.entity.RefreshToken;
import org.example.filexpressbackend.entity.Role;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.RoleRepository;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    public UserDto createUser(UserDto userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        if (userRepository.existsById(id)) {
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) return null;

            // Convert UserDto to User entity without changing password if it's blank
            User userToUpdate = convertToEntity(userDto);
            userToUpdate.setId(id); // Ensure the ID is set for updating
            userToUpdate.setUsername(userDto.getUsername());
            userToUpdate.setRoles(userToUpdate.getRoles());

            // Check if password is blank in the DTO; if so, keep the existing password
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                userToUpdate.setPassword(existingUser.getPassword());
            } else {
                userToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            User updatedUser = userRepository.save(userToUpdate);
            return convertToDto(updatedUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    private GrantedAuthority getAuthorities(Role role) {
        return new SimpleGrantedAuthority(role.getRoleName());
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, // Set password to null for security
                user.getDateOfBirth(),
                convertRolesToStrings(user.getRoles())
        );
    }

    private List<String> convertRolesToStrings(List<Role> roles) {
        return roles.stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList());
    }

    private User convertToEntity(UserDto userDto) {
        RefreshToken refreshToken = new RefreshToken();

        // Convert list of role names (Strings) to Role entities
        List<Role> roles = userDto.getRoles().stream()
            .map(roleName -> roleRepository.findByRoleName(roleName))
            .filter(Objects::nonNull) // Ensures no null values in case a role is not found
            .collect(Collectors.toList());

        // Create UserDetails with authorities derived from roles
        List<GrantedAuthority> authorities = roles.stream()
            .map(this::getAuthorities)
            .collect(Collectors.toList());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            userDto.getUsername(),
            userDto.getPassword() != null ? userDto.getPassword() : "", // Use empty password if null
            authorities
        );

        refreshToken.setToken(jwtUtil.generateRefreshToken(userDetails));

        // Create the User entity and set its fields
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword() != null ? passwordEncoder.encode(userDto.getPassword()) : null);
        user.setEmail(userDto.getEmail());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setRoles(roles);
        user.setRefreshToken(refreshToken);

        return user;
    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token.substring(7));
    }

    public String getEmailFromUsername(String username) {
        return userRepository.findByUsername(username).getEmail();
    }

    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}