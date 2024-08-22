package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.dto.ResourceResultData;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.data.repository.ResourceRepository;
import com.elastic.cspm.service.aws.CredentialInfo;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

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
    private final GroupHandler groupHandler;

    // IAM과 Group으로 스캔시간, AccountId, 리소스, 리소스ID, 서비스 조회
    public ResourceListDto getAllResources(ResourceFilterRequestDto resourceFilterDto) throws Exception {
        // DescriptionResult 정보 리스트 반환
        try {
            // 페이징
            Pageable pageable = PageRequest.of(resourceFilterDto.getPIndex(), resourceFilterDto.getPSize());

            Page<ResourceRecordDto> resources = resourceRepository.findResourceList(
                    pageable,
                    resourceFilterDto
            ).map(ResourceRecordDto::of);

            return new ResourceListDto(
                    resources.getContent(),
                    (int) resources.getTotalElements(),
                    resources.getTotalPages()
            );
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new Exception(String.valueOf(NOT_FOUND));
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
            createCredentials(Region.of(user.getRegion()), accessKey, secretKey);

            Boolean isAllSuccess = true;
            List<DescribeResult> describeEntityList = new ArrayList<>();

            // 스캔 시작
            DescribeResult result = groupHandler.groupDescribe(describeIamDto); // 자원 스캔 메소드 예시
            describeEntityList.add(result);

            describeEntityList.forEach(describeEntity -> {
                describeEntity.setIam(user);
            });

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


    private CredentialInfo createCredentials(Region region, String accessKey, String secretKey) {
        return new CredentialInfo(AwsBasicCredentials.create(accessKey, secretKey), region);
    }

    /**
     * 스캔하는 로직
     * 스캔 목적 : EC2Client로 data.sql에서 찾는 방법.
     *
     */
    private void groupDescribe() {

    }
}
