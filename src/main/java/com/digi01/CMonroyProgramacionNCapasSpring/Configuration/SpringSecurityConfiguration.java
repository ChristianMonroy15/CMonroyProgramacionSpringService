package com.digi01.CMonroyProgramacionNCapasSpring.Configuration;

import com.digi01.CMonroyProgramacionNCapasSpring.Security.JwtAuthenticationFilter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  PROVIDER (OBLIGATORIO)
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());  // ← BCrypt registrado
        return provider;
    }

    // AUTHENTICATION MANAGER 
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(List.of(authProvider()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                // PUBLICAS sin token ni nada
                .requestMatchers(
                        "/login",
                        "/auth/login",
                        "/guardarToken",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/api/direccion/**",
                        "/api/usuario/rol",
                        "/api/pais/**",
                        "/api/estado/**",
                        "/api/municipio/**",
                        "/api/colonia/**"
                ).permitAll()
                // ADMIN
                .requestMatchers(HttpMethod.POST, "/api/usuario/**").hasRole("Admin")
                .requestMatchers(HttpMethod.DELETE, "/api/usuario/**").hasRole("Admin")
                
                // LECTURA
                .requestMatchers(HttpMethod.GET, "/api/usuario/**").authenticated()
                // Cualquier otro /api requiere autenticación
                .requestMatchers("/api/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/usuario/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/usuario/**").authenticated()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
                )
                .authenticationProvider(authProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    //borarr    
    @PostConstruct
    public void init() {
        System.out.println("🔥 Seguridad CARGADA desde SpringSecurityConfiguration");
    }

}
