package com.queryx.recruiting_website.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;

// Security配置类
//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    // 定义认证逻辑
    @Bean
    public UserDetailsService userDetailsService(){
        // 1.使用内存数据进行认证
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 2.创建两个用户
        UserDetails user1 = User.withUsername("baizhan").password("123").authorities("admin").build();
        UserDetails user2 = User.withUsername("sxt").password("456").authorities("admin").build();
        // 3.将这两个用户添加到内存中
        manager.createUser(user1);
        manager.createUser(user2);
        return manager;
    }
    //密码编码器，不解析密码
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return NoOpPasswordEncoder.getInstance();
    }*/
}
