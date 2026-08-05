package rs.fon.probniispit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rs.fon.probniispit.security.JwtFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class BezbednosnaKonfiguracija {

    private final JwtFilter jwtFilter;

    public BezbednosnaKonfiguracija(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsKonfiguracija()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/termini/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cenovnik/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsKonfiguracija() {
        CorsConfiguration k = new CorsConfiguration();
        k.setAllowedOrigins(List.of("http://localhost:5173"));
        k.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        k.setAllowedHeaders(List.of("*"));
        k.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource izvor = new UrlBasedCorsConfigurationSource();
        izvor.registerCorsConfiguration("/**", k);
        return izvor;
    }
}