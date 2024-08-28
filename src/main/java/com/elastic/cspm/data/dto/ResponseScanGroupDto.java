package com.elastic.cspm.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseScanGroupDto {

    @NotNull
    private Integer updateNumber;


    private String afterResourceGroupName;


    private String beforeResourceGroupName;

    @NotNull
    private Boolean vpc;

    @NotNull
    private Boolean subnet;

    @NotNull
    private Boolean routeTable;

    @NotNull
    private Boolean internetGateway;

    @NotNull
    private Boolean instance;

    @NotNull
    private Boolean eni;

    @NotNull
    private Boolean ebs;

    @NotNull
    private Boolean s3;

    @NotNull
    private Boolean securityGroup;

    @NotNull
    private Boolean iam;

    @NotNull
    private Boolean rds;
}
