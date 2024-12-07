package com.chacha.config;

import com.chacha.security.CustomAuthenticationFailureHandler;
import com.chacha.security.CustomAuthenticationSuccessHandler;
import com.chacha.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig { // 실제 인증 처리하는 시큐리티 설정 파일

    private final UserDetailService userService;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;

    @Bean // 지정한 곳에 시큐리티 기능 비활성화
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                // .requestMatchers(toH2Console())
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Bean // 특정 HTTP 요청에 대한 웹 기반 보안 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //
        return http
                .authorizeRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll() // 해당 url만 누구나 접근 가능
                        .anyRequest().authenticated()) // 그 밖에 url은 인가는 필요없지만 인증은 필요함
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // 사용자가 인증되지 않은 상태에서 접속하면 리다이렉트
                        .successHandler(successHandler) // Success Handler 등록
                        .failureHandler(failureHandler) // Failure Handler 등록
                        //.successHandler(new CustomAuthenticationSuccessHandler()) // Success Handler 등록
                        //.defaultSuccessUrl("/menu", true) // true로 항상 리다이렉트
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // 로그아웃했을 때 이동할 경로
                        .invalidateHttpSession(true) // 세션 전체삭제 여부
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .build();
    }

    @Bean // 인증 관리자 관련 설정
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setHideUserNotFoundExceptions(false); // UsernameNotFoundException을 노출하도록 설정
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean // 패스워드 인코드 빈 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}