package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultData;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.data.repository.ResourceRepository;
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
    public List<ResourceResultData> startDescribe(List<DescribeIamDto> describeIamList) throws Exception {
        List<ResourceResultData> describeResultDataList = new ArrayList<>();
        log.info("describeIamList : {}", describeIamList);

        for (DescribeIamDto describeIamDto : describeIamList) {
            log.info("Processing DescribeIamDto: {}", describeIamDto);

            String iamName = describeIamDto.getIam(); // IAM 닉네임
            IAM user = iamRepository.findIAMByNickName(iamName);

            if(user == null) {
                log.error("No IAM user found with nickname: {}", iamName);
                continue;
            }

            String accessKey = aes256Util.decrypt(user.getAccessKey());
            String secretKey = aes256Util.decrypt(user.getSecretKey());
            String regionKey = aes256Util.decrypt(user.getRegion()); // 암호화해서 DB에 들어가기 때문에 복호화 해서 넣어줌.
            credentialManager.createCredentials(accessKey, secretKey, Region.of(regionKey));

            Boolean isAllSuccess = true;
            List<DescribeResult> describeEntityList = new ArrayList<>();

            // 스캔 시작 + Describe_Result에 저장 완료
            List<?> scanDescribe = groupScanDescribe(describeIamDto);
            log.info("scanDescribe : {}", scanDescribe);


            if(scanDescribe != null) {
                for(Object entity : scanDescribe){
                    if(entity instanceof DescribeResult describeEntity){
                        describeEntity.setIam(user); // IAM 정보 설정
                        describeEntityList.add(describeEntity);
                    } else{
                        isAllSuccess = false;
                    }
                }
            }
            else {
                isAllSuccess = false;
            }
            log.info("isAllSuccess : {}", isAllSuccess);


        }
        log.info("describe result: {}", describeResultDataList);


    }

    /**
     * 스캔하는 로직
     * 스캔 목적 : EC2Client로 group들 찾기
     */
    private List<?> groupScanDescribe(DescribeIamDto describeIamDto) {
        String scanGroup = describeIamDto.getGroupName();
        log.info("scanGroup : {}", scanGroup);
        return switch (scanGroup) {
            case "VPC" -> vpcDescribe(describeIamDto);
            case "EC2" -> ec2Describe(describeIamDto);
            case "S3" -> s3Describe(describeIamDto);
            case "Subnet" -> subnetDescribe(describeIamDto);
            case "RouteTable" -> routeDescribe(describeIamDto);
            case "InternetGateWay" -> internetGateWayDescribe(describeIamDto);
            case "Instance" -> instanceDescribe();
            case "ENI" -> eniDescribe(describeIamDto);
            case "EBS" -> ebsDescribe(describeIamDto);
            case "SecurityGroup" -> sgDescribe(describeIamDto);
            case "IAM" -> iamDescribe(describeIamDto);
            case "RDS" -> rdsDescribe(describeIamDto);

            default -> throw new IllegalArgumentException("지원되지 않는 스캔 그룹: " + scanGroup);
        };
    }

    private List<Vpc> vpcDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = Ec2Client.builder().build();
        List<Vpc> vpcList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (ec2Client) {
            log.info("Describing VPCs using: {}", ec2Client);

            DescribeVpcsRequest request = DescribeVpcsRequest.builder().build();
            DescribeVpcsResponse response = ec2Client.describeVpcs(request);

            List<Vpc> vpcs = response.vpcs();
            log.info("Described VPCs: {}", vpcs);

            for (Vpc vpc : vpcs) {
                saveVPCToDescribe(vpc, groupName);
            }
            return vpcList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe VPCs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveVPCToDescribe(Vpc vpc, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(vpc.vpcId());
        result.setScanTarget(vpc.cidrBlock()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    /**
     * EC2 스캔
     */
    private List<Instance> ec2Describe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<Instance> instanceList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();


        try (ec2Client) {
            log.info("EC2 describe : {}", ec2Client);
            // 요청
            DescribeInstancesRequest req = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse res = ec2Client.describeInstances(req);

            res.reservations().forEach(reservation -> {
                instanceList.addAll(reservation.instances());
            });

            log.info("instances : {}", instanceList);

            for (Instance instance : instanceList) {
                saveEC2ToDescribe(instance, groupName);
            }
            return instanceList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe EC2s: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    /**
     * EC2의 경우 Describe_Result에 저장하는 방식
     */
    private void saveEC2ToDescribe(Instance instance, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(instance.instanceId());
        result.setScanTarget(instance.keyName());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }


    private List<Bucket> s3Describe(DescribeIamDto describeIamDto) {
        S3Client s3Client = credentialManager.getS3Client();
        List<Bucket> s3List = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (s3Client) {
            log.info("S3 client : {}", s3Client.toString());
            // 요청
            ListBucketsRequest req = ListBucketsRequest.builder().build();
            ListBucketsResponse res = s3Client.listBuckets(req);

            List<Bucket> buckets = res.buckets();
            log.info("Buckets : {}", buckets);

            for (Bucket bucket : buckets) {
                log.info("Bucket Name: {}, Creation Date: {}",
                        bucket.name(),
                        bucket.creationDate());

                s3List.add(bucket);

                saveS3ToDescribe(bucket, groupName);
            }
            return s3List;
        } catch (S3Exception e) {
            log.error("Failed to describe S3 buckets: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveS3ToDescribe(Bucket bucket, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(bucket.name());
        result.setScanTarget(String.valueOf(PublicAccessBlockConfiguration.builder().build().blockPublicAcls())); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    /**
     * 서브넷 스캔
     */
    private List<Subnet> subnetDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<Subnet> subnetList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();


        try (ec2Client) {
            log.info("EC2 describe : {}", ec2Client);
            // 요청
            DescribeSubnetsRequest req = DescribeSubnetsRequest.builder().build();
            DescribeSubnetsResponse res = ec2Client.describeSubnets(req);

            subnetList.addAll(res.subnets());

            log.info("Subnets described: {}", subnetList);

            for (Subnet subnet : subnetList) {
                saveSubnetToDescribe(subnet, groupName);
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
    private void saveSubnetToDescribe(Subnet subnet, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(subnet.subnetId());
        result.setScanTarget(subnet.cidrBlock()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<RouteTable> routeDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<RouteTable> routeTableList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();


        try (ec2Client) {
            log.info("Describing Route Tables using: {}", ec2Client);

            DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder().build();
            DescribeRouteTablesResponse response = ec2Client.describeRouteTables(request);

            routeTableList.addAll(response.routeTables());

            log.info("Route Tables described: {}", routeTableList);

            for (RouteTable routeTable : routeTableList) {
                saveToRouteTableDescribe(routeTable, groupName);
            }

            return routeTableList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Route Tables: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveToRouteTableDescribe(Route route, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(route.gatewayId()); // 수정
        result.setScanTarget(route.destinationCidrBlock());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<InternetGateway> internetGateWayDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = Ec2Client.builder().build();
        List<InternetGateway> internetGatewayList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();
        try (ec2Client) {
            log.info("Describing Internet Gateways using: {}", ec2Client);

            DescribeInternetGatewaysRequest request = DescribeInternetGatewaysRequest.builder().build();
            DescribeInternetGatewaysResponse response = ec2Client.describeInternetGateways(request);

            internetGatewayList.addAll(response.internetGateways());

            log.info("Internet Gateways described: {}", internetGatewayList);

            for (InternetGateway igw : internetGatewayList) {
                saveInternetGatewayToDescribe(igw, groupName);
            }

            return internetGatewayList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Internet Gateways: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveInternetGatewayToDescribe(InternetGateway internetGateway, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(internetGateway.internetGatewayId());
        result.setScanTarget(internetGateway.attachments().toString()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<Instance> instanceDescribe() {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<Instance> instanceList = new ArrayList<>();

        try (ec2Client) {
            log.info("EC2 describe : {}", ec2Client);

            // 요청
            DescribeInstancesRequest req = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse res = ec2Client.describeInstances(req);

            res.reservations().forEach(reservation -> {
                instanceList.addAll(reservation.instances());
            });

            log.info("instances : {}", instanceList);

            for (Instance instance : instanceList) {

                // Extract and log security group names
                for (GroupIdentifier securityGroup : instance.securityGroups()) {
                    String groupName = securityGroup.groupName();
                    log.info("Instance ID: {}, Security Group Name: {}", instance.instanceId(), groupName);
                    saveInstanceToDescribe(instance, groupName);
                }
            }
            return instanceList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe EC2s: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveInstanceToDescribe(Instance instance, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(instance.instanceId());
        result.setScanTarget(instance.keyName());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<NetworkInterface> eniDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<NetworkInterface> eniList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (ec2Client) {
            log.info("Describing ENIs using : {}", ec2Client);

            DescribeNetworkInterfacesRequest request = DescribeNetworkInterfacesRequest.builder().build();
            DescribeNetworkInterfacesResponse response = ec2Client.describeNetworkInterfaces(request);

            eniList.addAll(response.networkInterfaces());

            log.info("ENIs described: {}", eniList);

            for (NetworkInterface eni : eniList) {
                saveENIToDescribe(eni, groupName);
            }

            return eniList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe ENIs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveENIToDescribe(NetworkInterface eni, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(eni.networkInterfaceId());
        result.setScanTarget(String.valueOf(eni.status()));
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<Volume> ebsDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<Volume> volumeList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (ec2Client) {
            log.info("Describing EBS volumes using : {}", ec2Client);

            DescribeVolumesRequest request = DescribeVolumesRequest.builder().build();
            DescribeVolumesResponse response = ec2Client.describeVolumes(request);

            volumeList.addAll(response.volumes());

            log.info("EBS volumes described: {}", volumeList);

            for (Volume volume : volumeList) {
                saveVolumeToDescribe(volume, groupName);
            }

            return volumeList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe EBS volumes: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveVolumeToDescribe(Volume volume, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(volume.volumeId());
        result.setScanTarget(volume.); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<SecurityGroup> sgDescribe(DescribeIamDto describeIamDto) {
        Ec2Client ec2Client = Ec2Client.builder().build(); // Replace with your credential management if needed
        List<SecurityGroup> securityGroupList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (ec2Client) {
            log.info("Describing Security Groups using: {}", ec2Client);

            DescribeSecurityGroupsRequest request = DescribeSecurityGroupsRequest.builder().build();
            DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups(request);

            securityGroupList.addAll(response.securityGroups());

            log.info("Security Groups described: {}", securityGroupList);

            for (SecurityGroup sg : securityGroupList) {
                saveSecurityGroupToDescribe(sg, groupName);
            }

            return securityGroupList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe Security Groups: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveSecurityGroupToDescribe(SecurityGroup sg, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(sg.groupId());
        result.setScanTarget(sg.ipPermissions().toString());
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<User> iamDescribe(DescribeIamDto describeIamDto) {
        IamClient iamClient = IamClient.builder().build();
        List<User> userList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (iamClient) {
            log.info("Describing IAM users using : {}", iamClient);

            ListUsersRequest request = ListUsersRequest.builder().build();
            ListUsersResponse response = iamClient.listUsers(request);

            userList.addAll(response.users());

            log.info("IAM users described: {}", userList);

            for (User user : userList) {
                saveUserToDescribe(user, groupName);
            }

            return userList;

        } catch (IamException e) {
            log.error("Failed to describe IAM users: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveUserToDescribe(User user, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(user.userId());
        result.setScanTarget(user.userName()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }

    private List<DBInstance> rdsDescribe(DescribeIamDto describeIamDto) {
        RdsClient rdsClient = RdsClient.builder().build();
        List<DBInstance> dbInstanceList = new ArrayList<>();
        String groupName = describeIamDto.getGroupName();

        try (rdsClient) {
            log.info("Describing RDS instances using : {}", rdsClient);

            DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder().build();
            DescribeDbInstancesResponse response = rdsClient.describeDBInstances(request);

            dbInstanceList.addAll(response.dbInstances());

            log.info("RDS instances described: {}", dbInstanceList);

            for (DBInstance dbInstance : dbInstanceList) {
                saveDBInstanceToDescribe(dbInstance, groupName);
            }

            return dbInstanceList;

        } catch (RdsException e) {
            log.error("Failed to describe RDS instances: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private void saveDBInstanceToDescribe(DBInstance dbInstance, String groupName) {
        DescribeResult result = new DescribeResult();
        result.setResourceId(dbInstance.dbiResourceId());
        result.setScanTarget(dbInstance.dbInstanceArn()); // 수정
        result.setGroupName(groupName);
        result.setScanTime(LocalDateTime.now());
        resourceRepository.save(result);
    }
}
