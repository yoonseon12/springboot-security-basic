package com.cos.security1.config.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행한다.
// 로그인이 진행되면 session을 만들어준다.(Security ContextHolder)
// 시큐리티가 가지고 있는 세션의 들어갈 수 있는 오브젝트가 정해져있다. => Authentication 객체
// Authentication 안에 User 정보가 있어야 된다.
// User 오브젝트 타입 => UserDetails 타입 객체

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 가지고 있는 Security Session에 들어갈 수 있는 객체가 Authentication 객체다.
// Authentication 안에 들어갈 수 있는 객체는 UserDatails 객체다.
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; // 콤포지션
    private Map<String, Object> attributes;

    // 일반 로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 유저의 권한을 리턴한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> user.getRole());
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { // 만료된 계정인지
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // 계정 잠금 상태인지
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 기간이 지난 계정인지 -> 오래 사용했는지
        return true;
    }

    @Override
    public boolean isEnabled() { // 계정 활성화 유무
        // 1년동안 로그인 안하면 휴면계정 하기로함.
        // 현재시간 - 로그인시간 => 1년 초과하면 return false; 이런걸 해줄 수 있다.
        return true;
    }

    @Override
    public String getName() {
//        return attributes.get("sub");
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
}
