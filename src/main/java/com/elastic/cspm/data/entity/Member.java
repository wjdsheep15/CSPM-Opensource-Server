package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Member")
public class Member {

    @Id // PK 값 직접 할당 해야 함.
    @Column(name = "email")
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @CreatedDate
    @Column(name="create_At", nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name="update_At", nullable = false)
    private LocalDateTime updateAt;

    @Column(name="role", nullable = false)
    private String role;

    @Column(name="iam_name", nullable = false)
    private String iamName; // IAM 계정 이름

    @Column(name="account_id", nullable = false)
    private String accountId;

    @OneToMany(mappedBy = "member")
    private List<ScanGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL, CascadeType.REMOVE})
    private List<IAM> iams = new ArrayList<>();
}
