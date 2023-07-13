package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize와 postAuthorize 어노테이션 활성화
public class SecurityConfig{

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/favicon.ico", "/error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()

                .authorizeRequests() // 요청에 대한 권한지정.  Security 처리에 HttpServletRequest를 이용한다는 것을 의미
                .antMatchers("/user/**").authenticated() // 인증만 되면 접근 가능
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 권한필요
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 권한필요
                .anyRequest().permitAll()

                .and()
                .formLogin()
                .loginPage("/loginForm")
                .usernameParameter("email") // loadUserByUsername(String username)의 파라미터명 변경할 수 있다.(이메일로 로그인이면 이메일이 될 수도있고)
                .loginProcessingUrl("/login") // login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행
                .defaultSuccessUrl("/"); // 로그인에 성공했을 때에 아무런 설정을 하지 않았을 시 넘어가는 페이지를 설정

        return httpSecurity.build();
    }
}
