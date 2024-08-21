package com.elastic.cspm.utils;

public enum DescribeType {
    VPC(Group.VPC),
    SUBNET(Group.VPC),
    SECURITY_GROUP(Group.VPC),
    ROUTE(Group.VPC),
    IGW(Group.VPC),
    INSTANCE(Group.EC2),
    EBS(Group.EC2),
    ENI(Group.EC2),
    S3(Group.S3);

    public enum Group {
        VPC,
        EC2,
        S3
    }

    private final Group group;

    DescribeType(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return this.group;
    }
}