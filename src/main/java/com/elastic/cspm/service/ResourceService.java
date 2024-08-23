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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.ArrayList;
import java.util.List;

import static com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceListDto;
import static com.elastic.cspm.data.dto.ResourceResultResponseDto.ResourceRecordDto;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final IamRepository iamRepository;
    private final AES256 aes256Util;
    private final CredentialManager credentialManager;
//    private final CredentialInfo credentialInfo;

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
//            throw new Exception(String.valueOf(NOT_FOUND));
            throw e;

        }
    }

    /**
     * 스캔 시작 비즈니스 로직
     */
    public ResponseEntity<Void> startDescribe(List<DescribeIamDto> describeIamList) throws Exception {
        List<ResourceResultData> describeResultDataList = new ArrayList<>();

        for (DescribeIamDto describeIamDto : describeIamList) {
            String iamName = describeIamDto.getIam(); // IAM 닉네임

            IAM user = iamRepository.findIAMByNickName(iamName);
            String accessKey = aes256Util.decrypt(user.getAccessKey());
            String secretKey = aes256Util.decrypt(user.getSecretKey());
            credentialManager.createCredentials(Region.of(user.getRegion()), accessKey, secretKey);

            Boolean isAllSuccess = true;
            List<DescribeResult> describeEntityList = new ArrayList<>();

            // 스캔 시작
            List<?> scanDescribe = groupScanDescribe(describeIamDto);

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

            // ResourceResultData 객체를 생성
            describeResultDataList.add(
                    ResourceResultData.of(isAllSuccess, describeEntityList));
        }
        log.info("describe result: {}", describeResultDataList);

        List<Boolean> isAllSuccessList = new ArrayList<>();
        for (ResourceResultData resultData : describeResultDataList) {
            // ResourceResult에 저장.
            resourceRepository.saveAll(resultData.getDescribeEntityList());
            isAllSuccessList.add(resultData.getIsAllSuccess());
        }

        return !isAllSuccessList.contains(false) ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * 스캔하는 로직
     * 스캔 목적 : EC2Client로 group들 찾기
     */
    private List<?> groupScanDescribe(DescribeIamDto describeIamDto) {
        String scanGroup = describeIamDto.getScanGroup();
        return switch (scanGroup) {
            case "VPC" -> vpcDescribe();
            case "EC2" -> ec2Describe();
            case "S3" -> s3Describe();
            default -> throw new IllegalArgumentException("지원되지 않는 스캔 그룹: " + scanGroup);
        };
    }

    private List<Vpc> vpcDescribe() {
        Ec2Client vpcClient = credentialManager.getEc2Client();
        List<Vpc> vpcList = new ArrayList<>();

        try (vpcClient) {
            log.info("vpc describe : {}", vpcClient);
            // 요청
            DescribeVpcsRequest req = DescribeVpcsRequest.builder().build();
            DescribeVpcsResponse res = vpcClient.describeVpcs(req);

            List<Vpc> vpcs = res.vpcs();
            log.info("vpcs : {}", vpcs.toString());

            for (Vpc vpc : vpcs) {
                log.info("VPC ID: {}, CIDR Block: {}, State: {}",
                        vpc.vpcId(),
                        vpc.cidrBlock(),
                        vpc.stateAsString());
                vpcList.add(vpc);
            }
            return vpcList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe VPCs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }

    private List<Instance> ec2Describe() {
        Ec2Client ec2Client = credentialManager.getEc2Client();
        List<Instance> instanceList = new ArrayList<>();

        try (ec2Client) {
            log.info("vpc describe : {}", ec2Client);
            // 요청
            DescribeInstancesRequest req = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse res = ec2Client.describeInstances(req);

            res.reservations().forEach(reservation -> {
                instanceList.addAll(reservation.instances());
            });

            log.info("instances : {}", instanceList);

            for (Instance instance : instanceList) {
                log.info("Instance ID: {}, State: {}",
                        instance.instanceId(),
                        instance.state().name());
            }
            return instanceList;

        } catch (Ec2Exception e) {
            log.error("Failed to describe VPCs: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }


    private List<Bucket> s3Describe() {
        S3Client s3Client = credentialManager.getS3Client();
        List<Bucket> s3List = new ArrayList<>();

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
            }
            return s3List;
        } catch (S3Exception e) {
            log.error("Failed to describe S3 buckets: {}", e.awsErrorDetails().errorMessage());
            return new ArrayList<>();
        }
    }
}
