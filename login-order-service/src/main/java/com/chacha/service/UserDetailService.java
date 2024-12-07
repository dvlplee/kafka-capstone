package com.chacha.service;

import com.chacha.domain.User;
import com.chacha.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
// 스프링 시큐리티에서 로그인할 때, 사용자 정보를 가져오는 인터페이스 UserDetailsService.
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override // 사용자이름(email)로 사용자의 정보를 가져오는 메서드.
    public User loadUserByUsername(String name) throws UsernameNotFoundException {
        return userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("Email not registered: " + name));
    }
}
