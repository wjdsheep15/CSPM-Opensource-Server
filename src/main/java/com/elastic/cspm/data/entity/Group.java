package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@Table(name="`Group`")
public class Group {
    @Id
    @Column(name = "resource_group_name")
    private String resourceGroupName;

    @Column(name = "vpc", nullable = false)
    private boolean vpc;

    @Column(name = "subnet", nullable = false)
    private boolean subnet;

    @Column(name = "route_table", nullable = false)
    private boolean routeTable;

    @Column(name = "internet_gate_way", nullable = false)
    private boolean internetGateway;

    @Column(name = "instance", nullable = false)
    private boolean instance;

    @Column(name = "eni", nullable = false)
    private boolean eni;

    @Column(name = "ebs", nullable = false)
    private boolean ebs;

    @Column(name = "s3", nullable = false)
    private boolean S3;

    @Column(name = "security_group", nullable = false)
    private boolean securityGroup;

    @Column(name = "iam", nullable = false)
    private boolean iam;

    @Column(name = "rds", nullable = false)
    private boolean rds;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "email")
    private Member member;

}
