package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;


@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "complianceResult", fetch = LAZY)
    private List<IAM> iamList = new ArrayList<>();

    @OneToMany(mappedBy = "complianceResult", fetch = LAZY)
    private List<Policy> policyList = new ArrayList<>();

}
