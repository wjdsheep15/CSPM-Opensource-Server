package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ComplianceResult")
public class ComplianceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="compliance_id")
    private Long id;

    @Column(name="scan_time", nullable = false)
    private LocalDateTime scanTime;

    @Column(name="status", nullable = false)
    private boolean status;

    @Column(name="resource_id", nullable = false)
    private String resourceId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "iam_id")
    private IAM iam;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "policyTitle")
    private Policy policy;


    public ComplianceResult(LocalDateTime scanTime, boolean status, String resourceId, IAM iam, Policy policy) {
        this.scanTime = scanTime;
        this.status = status;
        this.resourceId = resourceId;
        this.iam = iam;
        this.policy = policy;
    }
}
