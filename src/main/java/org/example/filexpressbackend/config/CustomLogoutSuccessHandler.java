package org.example.filexpressbackend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // Set response status to OK to indicate successful logout
        response.setStatus(HttpServletResponse.SC_OK);

        // Optionally, send a JSON response to indicate success
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Logout successful\"}");

        // Complete the logout process
        response.getWriter().flush();
    }
}