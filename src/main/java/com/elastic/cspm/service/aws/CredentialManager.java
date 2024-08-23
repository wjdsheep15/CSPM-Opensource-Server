package com.elastic.cspm.service.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;

@Component
@RequiredArgsConstructor
public class CredentialManager {
    private CredentialInfo credentialsInfo;

    /**
     * task : Credentials 생성
     */
    public void createCredentials(Region region, String accessKey, String secretKey) {
        credentialsInfo = new CredentialInfo(AwsBasicCredentials.create(accessKey, secretKey), region);
    }

    /**
     * task : Aws Ec2Client 접근 자원을 가져오기
     */
    public Ec2Client getEc2Client() {
        return Ec2Client.builder()
                .region(credentialsInfo.getRegion())
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        credentialsInfo.getCredentials().accessKeyId(),
                        credentialsInfo.getCredentials().secretAccessKey()))
                .build();
    }

    public S3Client getS3Client(){
        return S3Client.builder()
                .region(credentialsInfo.getRegion())
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        credentialsInfo.getCredentials().accessKeyId(),
                        credentialsInfo.getCredentials().secretAccessKey()))
                .build();
    }
}
