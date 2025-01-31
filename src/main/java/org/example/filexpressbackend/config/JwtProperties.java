package org.example.filexpressbackend.config;

/**
 * This class defines constants used for configuring JWT (JSON Web Tokens).
 */
public class JwtProperties {

    // Expiration time for access tokens (5 minutes in milliseconds)
    public static final int ACCESS_TOKEN_EXPIRATION_TIME = 50_000;

    // Additional idle time added to refresh token expiration (20 minutes in milliseconds)
    public static final int REFRESH_TOKEN_IDLE_TIME = 1_200_000;

    // Expiration time for refresh tokens (25 minutes in total)
    public static final int REFRESH_TOKEN_EXPIRATION_TIME = ACCESS_TOKEN_EXPIRATION_TIME + REFRESH_TOKEN_IDLE_TIME;

    // Prefix for the token in the Authorization header
    public static final String TOKEN_PREFIX = "Bearer ";

    // Key for the Authorization header in HTTP requests
    public static final String HEADER_STRING = "Authorization";

    /**
     * Private constructor to prevent instantiation.
     * This class should only be used for static constants.
     */
    private JwtProperties() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}