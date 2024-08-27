package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Table(name = "DescribeResult")
public class DescribeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "describe_id")
    private Long id;

    @Column(name = "scan_time", nullable = true)
    private LocalDateTime scanTime;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "scan_target", nullable = false)
    private String scanTarget; // resource로 스캔 대상

    @Column(name = "scan_group", nullable = false)
    private String scanGroup;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "iam_id")
    private IAM iam;
}
