package com.lifesup.gbtd.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JWTAuthenFilter extends AbstractAuthenticationProcessingFilter {

    private static final String BEARER = "Bearer";

    public JWTAuthenFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        log.info("{}: attemptAuthentication", this.getClass().getSimpleName());
        final String param = Optional.ofNullable(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                .orElse(httpServletRequest.getParameter("t"));

        final String token = Optional.ofNullable(param)
                .map(value -> StringUtils.removeStart(value, BEARER))
                .map(String::trim)
                .orElseThrow(() -> new BadCredentialsException("Missing Authentication Token"));

        final Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
        return super.getAuthenticationManager().authenticate(auth);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        log.info("{}: successfulAuthentication", this.getClass().getSimpleName());
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
