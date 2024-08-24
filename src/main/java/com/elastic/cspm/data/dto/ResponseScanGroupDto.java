package com.elastic.cspm.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseScanGroupDto {

    private Integer updateNumber;


    private String afterResourceGroupName;


    private String beforeResourceGroupName;

    private Boolean vpc;

    private Boolean subnet;

    private Boolean routeTable;

    private Boolean internetGateway;

    private Boolean instance;

    private Boolean eni;

    private Boolean ebs;

    private Boolean s3;


    private Boolean securityGroup;


    private Boolean iam;

    private Boolean rds;

}
