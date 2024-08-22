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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberRepository memberRepository;
    private final IamRepository iamRepository;
    private final GroupRepository groupRepository;
    private final EmailService emailService;
    private final AES256 aes256;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public InfoResponseDto validationAwsAccountId(String accessKey, String secretKey, String region) {

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


    public boolean signup(SignupDto signupDto) {
        boolean memberResult = false ;
        boolean iamResult = true ;
        String password = aes256.decrypt(signupDto.getPassword());

        try {
            Member member = new Member();
            member.setEmail(signupDto.getEmail());
            member.setPassword(bCryptPasswordEncoder.encode(password));
            member.setRole("ROLE_USER");
            member.setAccountId(signupDto.getAccountId());
            member.setIamName(signupDto.getUserName());
            memberRepository.save(member);

            memberResult = true ;
            iamResult = iamSave(signupDto.getEmail(), signupDto.getAccessKey(), signupDto.getSecretKey(), signupDto.getRegion());

            if( memberResult && iamResult ) {
                return true;
            }else {
                return false;
            }
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public Boolean iamSave( String email ,String accessKey, String secretKey, String regin) {

       try {
           IAM iam = new IAM();
           iam.setAccessKey(accessKey);
           iam.setSecretKey(secretKey);
           iam.setRegion(regin);
           iam.setNickName("default");
           iam.setMember(memberRepository.findById(email).orElse(null));
           iamRepository.save(iam);
           return true;
       }catch(Exception e) {
           return false;
       }
    }

    public String validationEmail(String email) {
        boolean member = memberRepository.existsById(email);
        if(member) {
            return "exit";
        }
        return emailService.sendEmailNotice(email);

    }

    public String SearchEmail(String accessKey) {
       return iamRepository.findEmailByAccessKey(accessKey).map(IAM::getMember).get().getEmail();
    }

    public Boolean upDatePassword(String email, String password){
        Member member =  memberRepository.findByEmail(email).orElse(null);
        if(member == null) {
            return false;
        }
        member.setPassword(bCryptPasswordEncoder.encode(password));
        memberRepository.save(member);
        return true;
    }
}
