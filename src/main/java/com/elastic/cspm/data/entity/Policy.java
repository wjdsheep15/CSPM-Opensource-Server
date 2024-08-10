package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.elastic.cspm.data.utils.Length.Lengths.SMALL;


@Entity
@Getter
@Setter
@Table(name = "Policy")
public class Policy {
    @Id
    @Column(name = "policy_title", length = SMALL)
    private String policyTitle;

    @Column(name = "pattern", nullable = false)
    private String pattern;

    @Column(name = "category", nullable = false)
    private String category; // 분류

    @Column(name = "serverity", nullable = false)
    private String serverity; // 심각도 등급

    @Column(name = "description", nullable = false)
    private String description; // 정책 설명

    @Column(name = "response", nullable = false)
    private String response; // 대응 방안

    @Column(name = "compliance", nullable = false)
    private String compliance; // 취약점 정책

    @OneToMany(mappedBy = "policy")
    private List<ComplianceResult> complianceResults;
}
