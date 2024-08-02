package com.elastic.cspm.data.describe_result.entity;

import com.elastic.cspm.data.iam.entity.IAM;
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
@Table(name = "DescribeResult")
public class DescribeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "describe_id")
    private Long id;

    @Column(name = "scan_time", nullable = false)
    private LocalDateTime scanTime;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "scan_target", nullable = false)
    private String scanTarget;

    @OneToMany(mappedBy = "describeResult", fetch = LAZY)
    private List<IAM> iamList = new ArrayList<>();
}
