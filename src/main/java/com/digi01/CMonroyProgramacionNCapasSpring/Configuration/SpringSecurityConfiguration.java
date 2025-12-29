package com.digi01.CMonroyProgramacionNCapasSpring.Configuration;

import com.digi01.CMonroyProgramacionNCapasSpring.Security.CustomAuthEntryPoint;
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
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthEntryPoint customAuthEntryPoint;

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

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
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthEntryPoint)
                )
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                // Preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Públicos
                .requestMatchers(
                        "/auth/login",
                        "/auth/forgot-password",
                        "/auth/reset-password",
                        "/api/verify",
                        "/api/resend-verification",
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
                // Admin
                .requestMatchers(HttpMethod.PATCH, "/api/usuario/update/status/**").hasRole("Admin")
                .requestMatchers(HttpMethod.POST, "/api/cargamasiva/**").hasRole("Admin")
                .requestMatchers(HttpMethod.POST, "/api/usuario/**").hasRole("Admin")
                .requestMatchers(HttpMethod.DELETE, "/api/usuario/**").hasRole("Admin")
                // Usuario autenticado
                .requestMatchers(HttpMethod.PATCH, "/api/usuario/imagen/update/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/usuario/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/usuario/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/usuario/**").authenticated()
                // Todo /api protegido
                .requestMatchers("/api/**").authenticated()
                // Resto
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
