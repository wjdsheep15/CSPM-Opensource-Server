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
    private String scanTarget; // resource로 스캔 대상

    @Column(name = "scan_group")
    private String scanGroup;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "iam_id")
    private IAM iam;

    // 명확하지 않음
    public void setIamNickName(String nickName) {
        if (this.iam == null) {
            this.iam = new IAM(); // IAM 객체가 없으면 새로 생성
        }
        this.iam.setNickName(nickName);
    }
}
