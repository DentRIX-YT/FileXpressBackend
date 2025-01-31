package org.example.filexpressbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*
    * The AuthenticationResponse class is used to store the access token, refresh token, and roles
    * It is used in the authentication controller for login
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private boolean privateKeyExists;
    private List<String> roles;

    public AuthenticationResponse(String accessToken, String refreshToken, boolean privateKeyExists,
                                  Collection<? extends GrantedAuthority> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.privateKeyExists = privateKeyExists;

        this.roles = roles.stream().map(GrantedAuthority::getAuthority).
                collect(Collectors.toList());
    }
}
