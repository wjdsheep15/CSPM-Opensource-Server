package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@Table(name = "IAM")
public class IAM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iam_id")
    private Long id;

    @Column(name="access_key", nullable = false)
    private String accessKey;

    @Column(name="secret_key", nullable = false)
    private String secretKey;

    @Column(name="region", nullable = false)
    private String region;

    @Column(name = "nick_name", nullable = true)
    private String nickName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="email")
    private Member member;

    @OneToMany(mappedBy = "iam", cascade = CascadeType.ALL)
    private List<ComplianceResult> complianceResults = new ArrayList<>();

    @OneToMany(mappedBy = "iam")
    private List<ErrorLog> errorLogs = new ArrayList<>();

    @OneToMany(mappedBy = "iam", cascade = CascadeType.ALL)
    private List<DescribeResult> describeResults = new ArrayList<>();
}