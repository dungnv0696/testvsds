package com.lifesup.gbtd.security;

import com.lifesup.gbtd.service.inteface.IAuthenticationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TokenProvider extends AbstractUserDetailsAuthenticationProvider {

    @NonNull
    private final IAuthenticationService auth;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // none
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        final Object token = authentication.getCredentials();
        return Optional
                .ofNullable(token)
                .map(String::valueOf)
                .flatMap(auth::findByToken)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user " + token));
    }
}
