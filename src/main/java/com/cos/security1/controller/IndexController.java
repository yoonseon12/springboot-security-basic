package com.cos.security1.controller;

import com.cos.security1.model.User;
import com.cos.security1.model.UserDto;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public @ResponseBody String index() {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user() {
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(UserDto userDto) {
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        User user = UserDto.toEntity(userDto, "ROLE_USER");
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/data")
    @Secured("ROLE_ADMIN")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @GetMapping("/info")
//    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole(ROLE_ADMIN)") // 여러개로 걸꺼면 이렇게
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public @ResponseBody String data() {
        return "데이터정보";
    }

}
