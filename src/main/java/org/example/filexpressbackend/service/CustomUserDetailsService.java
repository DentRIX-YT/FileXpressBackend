package org.example.filexpressbackend.service;


import lombok.RequiredArgsConstructor;
import org.example.filexpressbackend.entity.Role;
import org.example.filexpressbackend.entity.User;
import org.example.filexpressbackend.repository.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    mapRolesToAuthorities(user.getRoles())
            );
            if (!userDetails.isEnabled()) {
                throw new DisabledException("User account is disabled");
            }

            if (!userDetails.isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }

            return userDetails;

        } else {
           // throw new UsernameNotFoundException("Invalid username or password.");
            System.out.println("Invalid username or password, or logout out.");
            return null;
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
    }
}
