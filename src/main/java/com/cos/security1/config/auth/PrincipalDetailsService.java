package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// 시큐리티 설정에서 loginProcessingUrl("/login")
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IOC 되어있는 loadUserByUsername 함수가 실행
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 시큐리티 session 안에는 Authentication 안에는 UserDetails 있다.
    // 시큐리티 session(내부 Authentication(내부 UserDetails))가 된다.
    // 함수 종료 시 @AuthenticationPrincipal 어노테이션 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원없음"));

        return new PrincipalDetails(findUser);
    }
}
