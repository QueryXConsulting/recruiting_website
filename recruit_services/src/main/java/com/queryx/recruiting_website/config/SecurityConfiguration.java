package com.queryx.recruiting_website.config;

import com.queryx.recruiting_website.filter.DynamicSecurityFilter;
import com.queryx.recruiting_website.filter.JwtAuthenticationTokenFilter;
import com.queryx.recruiting_website.handler.security.AccessDeniedHandlerImpl;
import com.queryx.recruiting_website.handler.security.AuthenticationEntryPointImpl;
import com.queryx.recruiting_website.handler.security.LogoutHandlerImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private AuthenticationEntryPointImpl authenticationEntryPoint;
    @Resource
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Resource
    private DynamicSecurityFilter dynamicSecurityFilter;
    @Resource
    private LogoutHandlerImpl logoutHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        // 设置自定义权限评估器
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
//        handler.setPermissionEvaluator(customPermissionEvaluator);
        return handler;
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui/**",
                "/v3/**",
                "/avatar_files/**",
                "/offer_files/**",
                "/company/offer/save",
                "/signature_files/**"

        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .securityContext(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(it ->
                        // TODO 待配置其他路径
                        it.requestMatchers("/user/login", "/user/register", "/user/logout","/company/registerCompany").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(dynamicSecurityFilter, AuthenticationFilter.class)
                .logout(it -> it.logoutUrl("/user/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":200,\"message\":\"注销成功\"}");
                        })
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .cors(withDefaults());


//        httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll() // 允许所有请求
//                );
        return httpSecurity.build();
    }

}
