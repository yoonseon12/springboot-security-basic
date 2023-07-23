package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    public final PasswordEncoder bCryptPasswordEncoder;
    public final UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    // 함수 종료 시 @AuthenticationPrincipal 어노테이션 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration : "+userRequest.getClientRegistration()); // Registration로 어떤 OAuth로 로그인했는지 확인 가능
        System.out.println("getAccessToken : "+userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글 로그인버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> 회원 프로필 받아야함.(loadUser 함수) -> 회원 프로필
        System.out.println("getAttributes : "+oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getClientId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User user;
        Optional<User> findUser = userRepository.findByEmail(email);
        if(findUser.isEmpty()) {
            user = User.builder()
                    .username(username)
                    .password(password)
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .role(role)
                    .build();

            userRepository.save(user);
        } else {
            user = findUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
