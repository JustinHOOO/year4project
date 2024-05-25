package com.example.questionnaire.config;

import com.example.questionnaire.service.impl.UserManagementServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserManagementServiceImpl userManagementService;

    public MyWebSecurityConfig(UserManagementServiceImpl userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userManagementService);
    }

    @Override
java
复制代码
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/fillin/**", "/fillin/**", "/register", "/api/register").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("http://localhost/login")
                .loginProcessingUrl("/api/login")
                .successHandler(this::onAuthenticationSuccess)
                .failureHandler(this::onAuthenticationFailure)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .addLogoutHandler(this::onLogout)
                .logoutSuccessHandler(this::onLogoutSuccess)
                .and()
                .csrf().disable();
    }

    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(200);
            out.write("success");
        }
    }

    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(200);
            out.write("wrong");
        }
    }

    private void onLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Custom logout logic here
    }

    private void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Custom logout success logic here
    }
}