package com.elastic.cspm.data.dto;


import com.elastic.cspm.data.entity.ScanGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScanGroupDto {

    private String resourceGroupName;
    private boolean vpc;
    private boolean subnet;
    private boolean routeTable;
    private boolean internetGateway;
    private boolean instance;
    private boolean eni;
    private boolean ebs;
    private boolean S3;
    private boolean securityGroup;
    private boolean iam;
    private boolean rds;

    public static ScanGroupDto of(ScanGroup scanGroup) {
        return new ScanGroupDto(
                scanGroup.getResourceGroupName(),
                scanGroup.isVpc(),
                scanGroup.isSubnet(),
                scanGroup.isRouteTable(),
                scanGroup.isInternetGateway(),
                scanGroup.isInstance(),
                scanGroup.isEni(),
                scanGroup.isEbs(),
                scanGroup.isS3(),
                scanGroup.isSecurityGroup(),
                scanGroup.isIam(),
                scanGroup.isRds()
        );
    }
}
