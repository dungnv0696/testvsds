package com.lifesup.gbtd.config;

import com.lifesup.gbtd.security.ExceptionHandlerFilter;
import com.lifesup.gbtd.security.JWTAuthenFilter;
import com.lifesup.gbtd.security.MyCorsFilter;
import com.lifesup.gbtd.security.NoRedirectStrategy;
import com.lifesup.gbtd.security.TokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Objects;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final RequestMatcher PUBLIC_URLS =
            new OrRequestMatcher(
//                    new AntPathRequestMatcher("/api/**"),
//                    new AntPathRequestMatcher("/api/auth/**"),
                    new AntPathRequestMatcher("/api/login"),
                    new AntPathRequestMatcher("/mobile/login"),
                    new AntPathRequestMatcher("/mobile/request-otp"),
//                    new AntPathRequestMatcher("/api/loginBot"),
                    new AntPathRequestMatcher("/error")
            );

    private static final RequestMatcher PROTECTED_URLS = new NegatedRequestMatcher(PUBLIC_URLS);
    private final TokenProvider provider;

    public ServerSecurityConfig(final TokenProvider provider) {
        super();
        this.provider = Objects.requireNonNull(provider);
    }

    @Bean
    ExceptionHandlerFilter exceptionHandlerFilter() {
        return new ExceptionHandlerFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(provider);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PUBLIC_URLS);
    }

    @Bean
    JWTAuthenFilter restAuthenticationFilter() throws Exception {
        final JWTAuthenFilter filter = new JWTAuthenFilter(PROTECTED_URLS);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successHandler());
        return filter;
    }

    @Bean
    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new NoRedirectStrategy());
        return successHandler;
    }

    @Bean
    AuthenticationEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Disable Spring boot automatic filter registration.
     */
    @Bean
    FilterRegistrationBean<JWTAuthenFilter> disableAutoRegistration(final JWTAuthenFilter filter) {
        final FilterRegistrationBean<JWTAuthenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization", "t"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    MyCorsFilter myCorsFilter() {
        return new MyCorsFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .exceptionHandling()
//                .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
                .and()

//                .addFilterBefore(this.myCorsFilter(), SessionManagementFilter.class)
                .addFilterBefore(this.exceptionHandlerFilter(), AnonymousAuthenticationFilter.class)

                // TODO: 10/8/2020 uncomment for login
                .authenticationProvider(provider)
                .addFilterBefore(this.restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers(PROTECTED_URLS)
                .authenticated()
                .and()

                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
    }
}
