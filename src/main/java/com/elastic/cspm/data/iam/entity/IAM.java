package com.elastic.cspm.data.iam.entity;

import com.elastic.cspm.data.compliance_result.entity.ComplianceResult;
import com.elastic.cspm.data.describe_result.entity.DescribeResult;
import com.elastic.cspm.data.errorlog.entity.ErrorLog;
import com.elastic.cspm.data.member.entity.Member;
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

    @Column(name="scan_time", nullable = false)
    private String secretKey;

    @Column(name="region", nullable = false)
    private String region;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="email")
    private Member member;

    @OneToMany(mappedBy = "iam")
    private List<ComplianceResult> complianceResults = new ArrayList<>();

    @OneToMany(mappedBy = "iam")
    private List<ErrorLog> errorLogs = new ArrayList<>();

    @OneToMany(mappedBy = "iam")
    private List<DescribeResult> describeResults = new ArrayList<>();
}
