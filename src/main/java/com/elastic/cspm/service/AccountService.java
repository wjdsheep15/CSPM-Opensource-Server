package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.InfoResponseDto;
import com.elastic.cspm.data.dto.SignupDto;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.repository.GroupRepository;
import com.elastic.cspm.data.repository.IamRepository;
import com.elastic.cspm.data.repository.MemberRepository;
import com.elastic.cspm.utils.AES256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final IamRepository iamRepository;
    private final GroupRepository groupRepository;
    private final AES256 aes256;


    public InfoResponseDto getAwsAccountId(String accessKey, String secretKey, String region) {

        // 복호화
        String accessKeyDecrypt =  aes256.decrypt(accessKey);
        String secretKeyDecrypt =  aes256.decrypt(secretKey);
        String regionDecrypt = aes256.decrypt(region);
        InfoResponseDto infoResponseDto = new InfoResponseDto();

        // AWS 자격증 생성
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyDecrypt, secretKeyDecrypt);
        IamClient iamClient = IamClient.builder().region(Region.of(regionDecrypt)).build();
        StsClient stsClient = StsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(regionDecrypt))
                .build();

        try{
            GetCallerIdentityResponse callerIdentity = stsClient.getCallerIdentity(GetCallerIdentityRequest.builder().build());
            GetUserResponse getUserResponse = iamClient.getUser(GetUserRequest.builder().build());

            infoResponseDto.setAccountId(callerIdentity.account());
            infoResponseDto.setUserName(getUserResponse.user().userName());
            infoResponseDto.setStatus(0);

            return infoResponseDto;
        } catch (AwsServiceException  e) {
            infoResponseDto.setStatus(1);
            return infoResponseDto;
        }catch (SdkClientException e){
            infoResponseDto.setStatus(2);
            return infoResponseDto;
        }
    }


    public boolean signup(SignupDto signupDto) {
        Member member = new Member();
        member.setEmail(signupDto.getEmail());
        member.setPassword(signupDto.getPassword());
        member.setRole("user");
        member.setAccountId(signupDto.getAccountId());
        member.setIamName(signupDto.getUserName());
        memberRepository.save(member);

        iamSave(signupDto.getAccessKey(), signupDto.getSecretKey(), signupDto.getRegion());
        return true;
    }


    public List<String> iamSave(String accessKey, String secretKey, String regin) {
        List<String> accountList = new ArrayList<>();
        IAM iam = new IAM();
        iam.setAccessKey(accessKey);
        iam.setSecretKey(secretKey);
        iam.setRegion(regin);

        return accountList;
    }
}
