package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import static com.elastic.cspm.utils.Length.Lengths.SMALL;
import static java.awt.SystemColor.TEXT;

@Entity
@Getter
@Setter
@Table(name = "Policy")
public class Policy {
    @Id
    @Column(name = "policy_title", length = SMALL)
    private String policyTitle;

    @Column(name = "pattern", nullable = false, columnDefinition = "TEXT")
    private String pattern;

    @Column(name = "category", nullable = false)
    private String category; // 분류

    @Column(name = "severity", nullable = false)
    private String severity; // 심각도 등급

    @Column(name = "description", nullable = false)
    private String description; // 정책 설명

    @Column(name = "response", nullable = false, columnDefinition = "TEXT")
    private String response; // 대응 방안

    @Column(name = "compliance", nullable = false)
    private String compliance; // 취약점 정책

    @Column(name = "group_name", nullable = false)
    private String groupName; // groupName

    @OneToMany(mappedBy = "policy")
    private List<ComplianceResult> complianceResults;
}
