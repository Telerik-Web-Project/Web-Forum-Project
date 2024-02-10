package com.example.webproject.security;

import com.example.webproject.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;

    @Autowired
    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter, UserService userService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return daoProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("api/users/**").permitAll();
                    auth.requestMatchers("/users/**").permitAll();
                    auth.requestMatchers("/api/posts/**").permitAll();
                    auth.requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN");
                    auth.requestMatchers("/**", "/resources/**", "/static/**", "/css/**", "/js/**",
                                    "/images/**", "/vendor/**", "/fonts/**")
                            .permitAll();

                    auth.anyRequest().authenticated();

                })

                .formLogin(formLogin -> {
                    formLogin
                            .loginPage("/login")
                            .defaultSuccessUrl("/posts")
                            .permitAll();
                })

                .logout((logout) -> logout.logoutSuccessUrl("/"))

                .sessionManagement(Customizer.withDefaults())
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
