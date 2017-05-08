package com.soprasteria.initiatives.config;

import com.soprasteria.initiatives.web.ApiConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Bean modeling authenticated user
 *
 * @author jntakpe
 * @author cegiraud
 */
public class AuthenticatedUser implements Principal {

    public static final AuthenticatedUser EMPTY_USER = new AuthenticatedUser("", "", "", Collections.emptyList());

    private final String username;

    private String firstName;

    private String lastName;

    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(String username, String firstName, String lastName,
                             Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthenticatedUser)) {
            return false;
        }
        AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    @Override
    public String getName() {
        return username;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("username", username)
                .append("firstName", getFirstName())
                .append("lastName", getLastName())
                .append("authorities", getAuthorities())
                .toString();
    }

    /**
     * General Spring Security configuration
     *
     * @author jntakpe
     * @author cegiraud
     */
    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        private static final String OAUTH_AUTHORIZE = "/oauth/authorize";

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .requestMatchers().antMatchers(OAUTH_AUTHORIZE)
                    .and()
                    .authorizeRequests()
                    .antMatchers(OAUTH_AUTHORIZE).authenticated();
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers(ApiConstants.FAKE_SSO);
        }
    }
}
