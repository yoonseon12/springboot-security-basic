package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
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

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        } else {
            System.out.println("구글, 네이버 로그인 요청이 아님");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
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
            System.out.println("회원가입 된 회원입니다.");
            user = findUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
