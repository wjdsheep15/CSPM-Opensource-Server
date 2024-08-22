package com.elastic.cspm.service.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;

@Getter
@Setter
@AllArgsConstructor
@Component
public class CredentialInfo {
    /**
     * AwsBasicCredentials : AWS 서비스를 사용하기 위한 사용자 인증 정보
     */
    private AwsBasicCredentials credentials;
    private Region region;
}
