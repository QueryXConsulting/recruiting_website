package com.queryx.recruiting_website.config;

import com.queryx.recruiting_website.filter.DynamicSecurityFilter;
import com.queryx.recruiting_website.filter.JwtAuthenticationTokenFilter;
import com.queryx.recruiting_website.handler.security.AccessDeniedHandlerImpl;
import com.queryx.recruiting_website.handler.security.AuthenticationEntryPointImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
                "/avatar_files/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(it ->
                                it.requestMatchers("/admin/login", "/user/login", "/user/register").anonymous()
                                        // TODO 待修改
//                        .requestMatchers("/swagger-ui/*","/v3/**").permitAll()
                                        .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(dynamicSecurityFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置异常处理
        httpSecurity.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        ).cors(withDefaults()); // 跨域使用默认配置


//        httpSecurity
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll() // 允许所有请求
//                );
        return httpSecurity.build();
    }

}
