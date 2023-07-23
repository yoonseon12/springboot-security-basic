package com.cos.security1.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String password;

    private String username;

    private String role; //ROLE_USER, ROLE_ADMIN

    private LocalDateTime lastLoginDate;

    private String provider; // google, naver ...

    private String providerId;
    @CreatedDate
    private LocalDateTime createDate;

}