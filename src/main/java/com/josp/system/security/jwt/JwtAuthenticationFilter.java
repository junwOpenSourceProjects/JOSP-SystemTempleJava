package com.josp.system.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter for processing incoming requests.
 * Extends OncePerRequestFilter to ensure single execution per request.
 * 
 * <p>This filter:
 * <ul>
 *   <li>Extracts JWT token from Authorization header</li>
 *   <li>Validates token and retrieves username</li>
 *   <li>Loads user details via UserDetailsService</li>
 *   <li>Checks if user account is enabled (status=1)</li>
 *   <li>Sets authentication in SecurityContext if valid</li>
 * </ul>
 * 
 * <p>Uses ApplicationContext to lazily get UserDetailsService at runtime,
 * which breaks the circular dependency issue during Spring Bean initialization.
 *
 * @author JOSP Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ApplicationContext applicationContext;
    private final JwtTokenUtil jwtTokenUtil;
    private final String tokenHeader;
    private final String tokenHead;

    /**
     * Constructs a new JwtAuthenticationFilter.
     *
     * @param applicationContext the Spring application context for lazy bean retrieval
     * @param jwtTokenUtil the JWT token utility for token operations
     * @param tokenHeader the header name containing the token (e.g., "Authorization")
     * @param tokenHead the prefix before the actual token (e.g., "Bearer ")
     */
    public JwtAuthenticationFilter(ApplicationContext applicationContext,
                                   JwtTokenUtil jwtTokenUtil,
                                   String tokenHeader,
                                   String tokenHead) {
        this.applicationContext = applicationContext;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenHeader = tokenHeader;
        this.tokenHead = tokenHead;
    }

    /**
     * Filters incoming requests to perform JWT authentication.
     * 
     * <p>Process flow:
     * <ol>
     *   <li>Extract Authorization header</li>
     *   <li>Check if header starts with configured tokenHead (e.g., "Bearer ")</li>
     *   <li>Extract the actual token value</li>
     *   <li>Get username from token</li>
     *   <li>If no existing authentication, load user details</li>
     *   <li>Validate token against user details</li>
     *   <li>Check user is enabled (status=1) - reject disabled users with 401</li>
     *   <li>Set authentication in SecurityContext</li>
     * </ol>
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param chain the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            String authToken = authHeader.substring(this.tokenHead.length());
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            log.info("checking username:{}", username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Runtime retrieval of UserDetailsService to avoid circular dependency during Bean initialization
                UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Validate token
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    // Check if user is disabled - reject with 401 if account is disabled
                    if (!userDetails.isEnabled()) {
                        log.warn("user account is disabled: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"code\":401,\"message\":\"Account is disabled\",\"data\":null}");
                        return;
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.info("authenticated user:{}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
