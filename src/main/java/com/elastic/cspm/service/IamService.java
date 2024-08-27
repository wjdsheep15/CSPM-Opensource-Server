package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.IamAddDto;
import com.elastic.cspm.data.dto.IamSelectDto;
import com.elastic.cspm.data.dto.InfoResponseDto;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.data.repository.MemberRepository;
import com.elastic.cspm.utils.AES256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetUserRequest;
import software.amazon.awssdk.services.iam.model.GetUserResponse;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class IamService {
    private final IamRepository iamRepository;
    private final AES256 aes256;
    private final MemberRepository memberRepository;

    // IAM을 가져왔는데 Email통해서 같은지 확인을 하고 반환을 하도록 해야 함.
    public List<String> getIAMNicknames() {
        return iamRepository.findAll()
                .stream()
                .map(IAM::getNickName)
                .collect(Collectors.toList());
    }

    public InfoResponseDto validationIam(String accessKey, String secretKey, String region) {

        InfoResponseDto infoResponseDto = new InfoResponseDto();

        if (iamRepository.findAllByAccessKey(accessKey).isPresent()) {
            infoResponseDto.setStatus(3);
            return infoResponseDto;
        }

        // 복호화
        String accessKeyDecrypt = aes256.decrypt(accessKey);
        String secretKeyDecrypt = aes256.decrypt(secretKey);
        String regionDecrypt = aes256.decrypt(region);


        // AWS 자격증 생성
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyDecrypt, secretKeyDecrypt);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);

        IamClient iamClient = IamClient.builder()
                .credentialsProvider(credentialsProvider) // 자격 증명 제공자 명시적 설정
                .region(Region.of(regionDecrypt))
                .build();

        StsClient stsClient = StsClient.builder()
                .credentialsProvider(credentialsProvider) // 자격 증명 제공자 명시적 설정
                .region(Region.of(regionDecrypt))
                .build();

        try {
            GetCallerIdentityResponse callerIdentity = stsClient.getCallerIdentity(GetCallerIdentityRequest.builder().build());
            GetUserResponse getUserResponse = iamClient.getUser(GetUserRequest.builder().build());

            infoResponseDto.setAccountId(callerIdentity.account());
            infoResponseDto.setUserName(getUserResponse.user().userName());
            infoResponseDto.setStatus(0);

            return infoResponseDto;
        } catch (AwsServiceException e) {
            log.error("AWS 서비스 관련 예외 처리 : "+e.getMessage());
            infoResponseDto.setStatus(1);
            return infoResponseDto;
        } catch (SdkClientException e) {
            log.error(" AWS SDK 클라이언트 예외 처리 : " + e.getMessage());
            infoResponseDto.setStatus(2);
            return infoResponseDto;
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean add(IamAddDto iamAddDto , String email) {
        try {
            Member member = memberRepository.findByEmail(email).orElse(null);
            if (member == null){
                return false;
            }
            IAM iam = new IAM();
            iam.setAccessKey(iamAddDto.getAccessKey());
            iam.setSecretKey(iamAddDto.getSecretKey());
            iam.setRegion(iamAddDto.getRegion());
            iam.setNickName(iamAddDto.getNickName());
            iam.setMember(member);

            iamRepository.save(iam);

            return true;
        }catch (Exception e) {
            log.error("IAM 추가 실패  : " + e.getMessage());
            return false;
        }
    }


    public ResponseEntity<Void> iamDelete(List<IamSelectDto> iamSelectDtoList){
        for(IamSelectDto selectDto: iamSelectDtoList){
            IAM iam = iamRepository.findIAMByNickName(selectDto.getNickname());

            if(iam == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }else {
                iamRepository.delete(iam);
            }
        }
        return ResponseEntity.ok().build();
    }
}
