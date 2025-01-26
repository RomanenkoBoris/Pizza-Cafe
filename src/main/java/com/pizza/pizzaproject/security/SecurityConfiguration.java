package com.pizza.pizzaproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
    @Autowired
    private UserDetailSource userDetailSource;

    @Bean
    public static BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(getEncoder());
        provider.setUserDetailsService(userDetailSource);
        return provider;
    }

    @Bean
    public SecurityFilterChain getChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(toH2Console()).permitAll()
                                .requestMatchers(HttpMethod.GET, "/index.html").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/users/*").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/users/*").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/users/*").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/pizzas").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/pizzas").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/pizzas").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/pizzas").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/cafes").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/cafes").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/cafes/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/cafes/{id}/pizzas").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/cafes/{cafeId}/pizzas/{pizzaId}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/cafes/{id}").hasRole("ADMIN")
                                .anyRequest().permitAll()


                )
                .formLogin()
                .and()
                .csrf()
                .disable()
                .headers().frameOptions().disable()
                .and()
                .httpBasic(Customizer.withDefaults())
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        return http.build();
    }

}
