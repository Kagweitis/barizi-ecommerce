package com.barizi.ecommerce.barizi.Config;



import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers(
                                "/swagger-ui/index.html#/",
                                "/swagger-*/**", "/v2/api-docs/**",
                                "/v3/api-docs/**", "auth/api/v1/register", "auth/api/v1/login", "api/v1/orders/products"
                        )
                        .permitAll()
                        .requestMatchers("api/v1/categories/new/category").hasAuthority("ADMIN")
                        .requestMatchers("api/v1/products/search/products").hasAuthority("CUSTOMER")
                        .requestMatchers("api/v1/products/**").hasAuthority("ADMIN")
                        .requestMatchers("api/v1/orders/**").hasAnyAuthority("CUSTOMER", "ADMIN")
//                        .requestMatchers("/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
