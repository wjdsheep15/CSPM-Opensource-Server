package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultData;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.ScanGroup;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.data.repository.ResourceRepository;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import com.elastic.cspm.service.aws.CredentialManager;
import com.elastic.cspm.utils.AES256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListUsersRequest;
import software.amazon.awssdk.services.iam.model.ListUsersResponse;
import software.amazon.awssdk.services.iam.model.User;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.RdsException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.rds.model.DBInstance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceListDto;
import static com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceRecordDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final IamRepository iamRepository;
    private final AES256 aes256Util;
    private final CredentialManager credentialManager;
    private final ScanGroupRepository scanGroupRepository;

    // IAM과 Group으로 스캔시간, AccountId, 리소스, 리소스ID, 서비스 조회
    public ResourceListDto getAllResources(ResourceFilterRequestDto resourceFilterDto) throws Exception {
        // DescriptionResult 정보 리스트 반환
        try {
            // 페이지 인덱스와 필터링 요청이 필요.
            Pageable pageable = PageRequest.
                    of(resourceFilterDto.getPageIndex(), resourceFilterDto.getPageSize());
            log.info("페이징 : {}", pageable);

            Page<ResourceRecordDto> resources = resourceRepository.findResourceList(
                    pageable,
                    resourceFilterDto
            ).map(ResourceRecordDto::of);

            log.info("resources : {}", resources);
            log.info("Content: {}", resources.getContent());
            log.info("Total Elements: {}", resources.getTotalElements());
            log.info("Total Pages: {}", resources.getTotalPages());


            return new ResourceListDto(
                    resources.getContent(),
                    (int) resources.getTotalElements(),
                    resources.getTotalPages()
            );
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    /**
     * 스캔 시작 비즈니스 로직
     */
    public List<ResourceResultData> startDescribe(ResourceFilterRequestDto resourceFilterRequestDto) throws Exception {
        List<ResourceResultData> describeResultDataList = new ArrayList<>();
        log.info("resourceFilter : {}", resourceFilterRequestDto);

        String iamName = resourceFilterRequestDto.getIam(); // IAM 닉네임
        IAM user = iamRepository.findIAMByNickName(iamName);


        String accessKey = aes256Util.decrypt(user.getAccessKey());
        String secretKey = aes256Util.decrypt(user.getSecretKey());
        String regionKey = aes256Util.decrypt(user.getRegion()); // 암호화해서 DB에 들어가기 때문에 복호화 해서 넣어줌.
        credentialManager.createCredentials(accessKey, secretKey, Region.of(regionKey));

        Boolean isAllSuccess = true;
        List<DescribeResult> describeEntityList = new ArrayList<>();

        ResourceResultData resourceResultData = ResourceResultData.of(
                LocalDateTime.now(),
                resourceFilterRequestDto.getIam(),
                resourceFilterRequestDto.getGroupName(),
                isAllSuccess,
                describeEntityList
        );

        // 스캔 시작 + Describe_Result에 저장 완료
        try {
            ScanGroup group = scanGroupRepository.findByResourceGroupName(resourceResultData.getResourceId()).orElse(null);
            log.info("group : {}", group);
            if(group != null) {
                log.info("scanDescribe : {}", group);
                List<DescribeResult> describeResults = groupScanDescribe(group);
                log.info("describeResultDataList : {}", describeResults);
                describeEntityList.addAll(describeResults);
            }


        } catch (Exception e) {
            log.error("Error during scan: {}", e.getMessage());
            isAllSuccess = false; // 오류 발생 시 isAllSuccess를 false로 설정
        }

        describeResultDataList.add(resourceResultData);
        log.info("isAllSuccess : {}", isAllSuccess);
        log.info("isAllSuccess : {}", describeEntityList);

        return describeResultDataList;
    }


    /**
     * 스캔하는 로직
     * 스캔 목적 : EC2Client로 group들 찾기
     */
    private List<DescribeResult> groupScanDescribe(ScanGroup group) {
        List<DescribeResult> result = new ArrayList<>();

        String scanGroupName = group.getResourceGroupName();

        switch (scanGroupName) {
            case "VPC" -> {
                result.addAll(vpcDescribe(scanGroupName)); // VPC 정보 추가
                result.addAll(subnetDescribe(scanGroupName)); // 서브넷 정보 추가
                result.addAll(sgDescribe(scanGroupName)); // 보안 그룹 정보 추가
                result.addAll(routeDescribe(scanGroupName)); // 라우트 정보 추가
                result.addAll(internetGateWayDescribe(scanGroupName)); // 인터넷 게이트웨이 정보 추가

            }
            case "EC2" -> {
                result.addAll(instanceDescribe(scanGroupName));
                result.addAll(ebsDescribe(scanGroupName));
                result.addAll(eniDescribe(scanGroupName));
            }
            case "S3" -> {
                result.addAll(s3Describe(scanGroupName));
            }
            case "default" -> {
                result.addAll(vpcDescribe(scanGroupName));
                result.addAll(subnetDescribe(scanGroupName));
                result.addAll(sgDescribe(scanGroupName));
                result.addAll(routeDescribe(scanGroupName));
                result.addAll(internetGateWayDescribe(scanGroupName));

                result.addAll(instanceDescribe(scanGroupName));
                result.addAll(ebsDescribe(scanGroupName));
                result.addAll(eniDescribe(scanGroupName));
                result.addAll(s3Describe(scanGroupName));
                result.addAll(rdsDescribe(scanGroupName));
                result.addAll(iamDescribe(scanGroupName));
            }
            default -> throw new IllegalStateException("Unexpected value: " + scanGroupName);
        };

        return result;
    }


    private List<DescribeResult> vpcDescribe(String scanGroupName) {
        Ec2Client ec2Client = Ec2Client.builder().build();
        List<DescribeResult> vpcList = new ArrayList<>();

        try (ec2Client) {
            DescribeVpcsRequest request = DescribeVpcsRequest.builder().build();
            DescribeVpcsResponse response = ec2Client.describeVpcs(request);

            List<Vpc> vpcs = response.vpcs();
            for (Vpc vpc : vpcs) {
                log.info("VPC 저장 완료: {}", vpc.vpcId());
                vpcList.add(saveVPCToDescribe(vpc, scanGroupName));
            }

            log.info("vpcList : {}", vpcList);
            return vpcList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe VPCs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveVPCToDescribe(Vpc vpc, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(vpc.vpcId());
        result.setScanTarget(vpc.cidrBlock()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
        return result;
    }


    private List<DescribeResult> s3Describe(String scanGroupName) {
        S3Client s3Client = credentialManager.getS3Client();
        List<DescribeResult> s3List = new ArrayList<>();

        try (s3Client) {
            log.info("S3 client : {}", s3Client.toString());
            ListBucketsRequest req = ListBucketsRequest.builder().build();
            ListBucketsResponse res = s3Client.listBuckets(req);

            List<Bucket> buckets = res.buckets();
            log.info("Buckets : {}", buckets);

            for (Bucket bucket : buckets) {
                s3List.add(saveS3ToDescribe(bucket, scanGroupName));
            }
            return s3List;
        } catch (S3Exception e) {
            log.error("Failed to describe S3 buckets: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveS3ToDescribe(Bucket bucket, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(bucket.name());
        result.setScanTarget(String.valueOf(PublicAccessBlockConfiguration.builder().build().blockPublicAcls())); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
        return result;
    }

    /**
     * 서브넷 스캔
     */
    private List<DescribeResult> subnetDescribe(String scanGroupName) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<DescribeResult> subnetList = new ArrayList<>();


        try (ec2Client) {
            log.info("EC2 describe : {}", ec2Client);
            DescribeSubnetsRequest req = DescribeSubnetsRequest.builder().build();
            DescribeSubnetsResponse res = ec2Client.describeSubnets(req);

            List<Subnet> subnets = res.subnets();


            for (Subnet subnet : subnets) {
                DescribeResult describeResult = saveSubnetToDescribe(subnet, scanGroupName);
                subnetList.add(describeResult);
            }

            log.info("Subnets described: {}", subnetList);
            return subnetList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe subnets: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    /**
     * subnet일 경우 저장
     */
    private DescribeResult saveSubnetToDescribe(Subnet subnet, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(subnet.subnetId());
        result.setScanTarget(subnet.cidrBlock()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> routeDescribe(String scanGroupName) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<DescribeResult> routeList = new ArrayList<>();


        try (ec2Client) {
            log.info("Describing Route Tables using: {}", ec2Client);

            DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder().build();
            DescribeRouteTablesResponse response = ec2Client.describeRouteTables(request);

            List<RouteTable> routeTables = response.routeTables();

            log.info("Route Tables described: {}", routeList);

            for (RouteTable route : routeTables) {
                DescribeResult describeResult = saveToRouteTableDescribe(route, scanGroupName);
                routeList.add(describeResult);
            }

            return routeList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Route Tables: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveToRouteTableDescribe(RouteTable routeTable, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(routeTable.routeTableId());
        result.setScanTarget(routeTable.routes().toString()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> internetGateWayDescribe(String scanGroupName) {
        Ec2Client ec2Client = Ec2Client.builder().build();
        List<DescribeResult> internetGatewayList = new ArrayList<>();
        try (ec2Client) {
            log.info("Describing Internet Gateways using: {}", ec2Client);

            DescribeInternetGatewaysRequest request = DescribeInternetGatewaysRequest.builder().build();
            DescribeInternetGatewaysResponse response = ec2Client.describeInternetGateways(request);

            List<InternetGateway> internetGateways = response.internetGateways();

            for (InternetGateway igw : internetGateways) {
                internetGatewayList.add(saveInternetGatewayToDescribe(igw, scanGroupName));
            }

            return internetGatewayList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Internet Gateways: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveInternetGatewayToDescribe(InternetGateway internetGateway, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(internetGateway.internetGatewayId());
        result.setScanTarget(internetGateway.attachments().toString()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> instanceDescribe(String scanGroupName) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<DescribeResult> instanceList = new ArrayList<>();

        try (ec2Client) {
            log.info("EC2 describe : {}", ec2Client);

            // 요청
            DescribeInstancesRequest req = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse res = ec2Client.describeInstances(req);

            List<Instance> list = res.reservations().stream().flatMap(reservation -> reservation.instances().stream()).toList();

            log.info("instances : {}", instanceList);

            for (Instance instance : list) {
                DescribeResult describeResult = saveInstanceToDescribe(instance, scanGroupName);
                instanceList.add(describeResult);
            }
            return instanceList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe EC2s: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveInstanceToDescribe(Instance instance, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(instance.instanceId());
        result.setScanTarget(instance.keyName());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> eniDescribe(String scanGroupName) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<DescribeResult> eniList = new ArrayList<>();

        try (ec2Client) {
            log.info("Describing ENIs using : {}", ec2Client);

            DescribeNetworkInterfacesRequest request = DescribeNetworkInterfacesRequest.builder().build();
            DescribeNetworkInterfacesResponse response = ec2Client.describeNetworkInterfaces(request);

            List<NetworkInterface> networkInterfaces = response.networkInterfaces();

            log.info("ENIs described: {}", eniList);

            for (NetworkInterface eni : networkInterfaces) {
                DescribeResult describeResult = saveENIToDescribe(eni, scanGroupName);
                eniList.add(describeResult);
            }

            return eniList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe ENIs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveENIToDescribe(NetworkInterface eni, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(eni.networkInterfaceId());
        result.setScanTarget(eni.privateIpAddresses().get(0).privateIpAddress());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> ebsDescribe(String scanGroupName) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<DescribeResult> volumeList = new ArrayList<>();

        try (ec2Client) {
            log.info("Describing EBS volumes using : {}", ec2Client);

            DescribeVolumesRequest request = DescribeVolumesRequest.builder().build();
            DescribeVolumesResponse response = ec2Client.describeVolumes(request);

            List<Volume> volumes = response.volumes();

            log.info("EBS volumes described: {}", volumeList);

            for (Volume volume : volumes) {
                DescribeResult describeResult = saveVolumeToDescribe(volume, scanGroupName);
                volumeList.add(describeResult);
            }

            return volumeList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe EBS volumes: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveVolumeToDescribe(Volume volume, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(volume.volumeId());
        result.setScanTarget(volume.state().toString()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> sgDescribe(String scanGroupName) {
        Ec2Client ec2Client = Ec2Client.builder().build(); // Replace with your credential management if needed
        List<DescribeResult> securityGroupList = new ArrayList<>();

        try (ec2Client) {
            log.info("Describing Security Groups using: {}", ec2Client);

            DescribeSecurityGroupsRequest request = DescribeSecurityGroupsRequest.builder().build();
            DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups(request);

            List<SecurityGroup> securityGroups = response.securityGroups();

            log.info("Security Groups described: {}", securityGroupList);

            for (SecurityGroup sg : securityGroups) {
                DescribeResult describeResult = saveSecurityGroupToDescribe(sg, scanGroupName);
                securityGroupList.add(describeResult);
            }

            return securityGroupList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Security Groups: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveSecurityGroupToDescribe(SecurityGroup sg, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(sg.groupId());
        result.setScanTarget(sg.ipPermissions().toString());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> iamDescribe(String scanGroupName) {
        IamClient iamClient = IamClient.builder().build();
        List<DescribeResult> userList = new ArrayList<>();

        try (iamClient) {
            log.info("Describing IAM users using : {}", iamClient);

            ListUsersRequest request = ListUsersRequest.builder().build();
            ListUsersResponse response = iamClient.listUsers(request);

            List<User> users = response.users();

            log.info("IAM users described: {}", userList);

            for (User user : users) {
                DescribeResult describeResult = saveUserToDescribe(user, scanGroupName);
                userList.add(describeResult);
            }

            return userList;

        } catch (IamException e) {
            log.error("Failed to describe IAM users: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private DescribeResult saveUserToDescribe(User user, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(user.userId());
        result.setScanTarget(user.userName()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }

    private List<DescribeResult> rdsDescribe(String scanGroupName) {
        RdsClient rdsClient = RdsClient.builder().build();
        List<DescribeResult> dbInstanceList = new ArrayList<>();

        try (rdsClient) {
            log.info("Describing RDS instances using : {}", rdsClient);

            DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder().build();
            DescribeDbInstancesResponse response = rdsClient.describeDBInstances(request);

            List<DBInstance> dbInstances = response.dbInstances();

            log.info("RDS instances described: {}", dbInstanceList);

            for (DBInstance dbInstance : dbInstances) {
                DescribeResult describeResult = saveDBInstanceToDescribe(dbInstance, scanGroupName);
                dbInstanceList.add(describeResult);
            }

            return dbInstanceList;

        } catch (RdsException e) {
            log.error("Failed to describe RDS instances: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }


    private DescribeResult saveDBInstanceToDescribe(DBInstance dbInstance, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(dbInstance.dbiResourceId());
        result.setScanTarget(dbInstance.dbInstanceArn()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);

        return result;
    }
}
