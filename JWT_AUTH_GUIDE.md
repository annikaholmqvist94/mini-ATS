# 🔐 JWT Authentication Guide (Optional - För Produktion)

**OBS:** Detta är INTE nödvändigt för development!
Din nuvarande SecurityConfig är perfekt för att komma igång.

Använd denna guide när du vill lägga till riktig autentisering för produktion.

---

## Alternativ 1: Supabase Auth (Enklast)

### Frontend (Lovable)
```typescript
import { createClient } from '@supabase/supabase-js'

const supabase = createClient(
  'YOUR_SUPABASE_URL',
  'YOUR_SUPABASE_ANON_KEY'
)

// Login
const { data, error } = await supabase.auth.signInWithPassword({
  email: 'user@example.com',
  password: 'password'
})

// Get JWT token
const token = data.session.access_token

// Use token in API calls
fetch('http://localhost:8080/api/jobs', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

### Backend JWT Filter

**1. Skapa JwtAuthenticationFilter:**

```java
package com.miniats.security;

import com.miniats.config.SupabaseConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SupabaseConfig supabaseConfig;

    public JwtAuthenticationFilter(SupabaseConfig supabaseConfig) {
        this.supabaseConfig = supabaseConfig;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        
        try {
            // Verify JWT token from Supabase
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(supabaseConfig.getSupabaseServiceRoleKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
            
            String email = claims.getSubject();
            
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("JWT validation failed", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**2. Uppdatera SecurityConfig:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## Alternativ 2: Basic Auth (Snabbt för demo)

Om du bara vill ha minimal säkerhet snabbt:

**1. Lägg till i application.yml:**

```yaml
spring:
  security:
    user:
      name: admin
      password: demo123
```

**2. Uppdatera SecurityConfig:**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/health/**").permitAll()
    .anyRequest().authenticated()
)
.httpBasic(Customizer.withDefaults());
```

**3. Frontend:**
```typescript
const credentials = btoa('admin:demo123');

fetch('http://localhost:8080/api/jobs', {
  headers: {
    'Authorization': `Basic ${credentials}`
  }
})
```

---

## När ska du implementera detta?

**NU (Development):**
- ❌ Inte nödvändigt
- ✅ Använd nuvarande permitAll() config
- ✅ Fokusera på att bygga features

**INNAN PRODUKTION:**
- ✅ Implementera Supabase Auth
- ✅ Lägg till JWT filter
- ✅ Test autentisering
- ✅ Lägg till role-based authorization (ADMIN vs USER)

---

## Tips för Produktion

### 1. Role-Based Access Control
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/health/**").permitAll()
    .requestMatchers("/api/organizations/**").hasRole("ADMIN")
    .requestMatchers("/api/users/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### 2. Rate Limiting
Använd Spring libraries för att begränsa requests:
```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.0</version>
</dependency>
```

### 3. HTTPS Only
I produktion, tvinga HTTPS:
```java
http.requiresChannel(channel -> 
    channel.anyRequest().requiresSecure()
);
```

### 4. Environment-Based Config
```java
@Profile("production")
@Configuration
public class ProductionSecurityConfig {
    // Strict security for production
}

@Profile("development")
@Configuration
public class DevelopmentSecurityConfig {
    // Permissive for development
}
```

---

## Sammanfattning

**FÖR NU:**
- ✅ Använd `permitAll()` i SecurityConfig
- ✅ Bygg features utan att oroa dig för auth
- ✅ CORS är konfigurerat och fungerar

**FÖR PRODUKTION:**
- ⬜ Implementera Supabase JWT auth
- ⬜ Lägg till JwtAuthenticationFilter
- ⬜ Test autentisering
- ⬜ Lägg till RBAC (role-based access control)
- ⬜ Enable HTTPS
- ⬜ Add rate limiting

**Gå vidare med frontend development nu!**