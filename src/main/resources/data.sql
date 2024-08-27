INSERT INTO oscspm.policy (
    policy_title,
    category,
    severity,
    compliance,
    description,
    response,
    pattern,
    group_name
)
VALUES
    (
        '보안그룹 Inbound Any open(0.0.0.0) 점검',
        '접근 관리',
        'High',
        'PCI DSS 1.2.1, Nist.800-53.R5 AC-4, CIS AWS Foundations Benchmark 4.1',
        '사용 중인 인스턴스의 보안 그룹에서 인바운드 정책이 0.0.0.0/0으로 오픈되어 있는 경우, 인터넷의 모든 IP 주소에서 해당 인스턴스로의 접근이 허용되어 있음을 의미합니다. 이렇게 되면 인스턴스가 보안 위협에 노출될 수 있습니다.',
        '1. 필요한 IP 주소 또는 IP 주소 범위만 허용하도록 인바운드 정책을 수정합니다. 이를 통해 신뢰할 수 있는 IP 주소만 인스턴스에 접근할 수 있습니다. 2. 보안 그룹에서 특정 포트만 허용하도록 설정합니다. 예를 들어, SSH 포트(22)를 허용하되, 다른 포트를 차단하도록 설정할 수 있습니다. 3. VPN 또는 프라이빗 링크를 사용하여 안전하게 연결하도록 네트워크를 구성합니다. 이렇게 하면 인터넷을 통한 접근을 완전히 차단할 수 있습니다. 4. 정기적인 보안 그룹 리뷰를 수행하여 인바운드 정책을 엄격하게 관리하고, 필요하지 않은 접근을 차단합니다. 5. 네트워크 ACL을 사용하여 보안 그룹과 별도로 보안 계층을 추가합니다.',
        'WHERE p.ip_permissions LIKE ''%CidrIp=0.0.0.0/0%',
        'SG'
    ),
    (
        '보안그룹 Outbound Any open(0.0.0.0) 점검',
        '접근 관리',
        'Medium',
        'PCI DSS 6.5.10, ISO/IEC 27001:2013 A.9.4, NIST SP 800-53 SC-13, CIS AWS Foundations Benchmark 4.1',
        '사용 중인 인스턴스의 보안 그룹에서 아웃바운드 정책이 0.0.0.0/0으로 오픈되어 있는 경우, 모든 IP 주소로의 아웃바운드 트래픽이 허용됩니다. 이는 민감한 데이터가 외부로 유출될 수 있는 보안 위협을 초래할 수 있습니다.',
        '1. 필요한 IP 주소 또는 IP 주소 범위만 허용하도록 아웃바운드 정책을 수정합니다. 이를 통해 신뢰할 수 있는 IP 주소로만 아웃바운드 트래픽을 제한할 수 있습니다. 2. 보안 그룹에서 특정 포트만 허용하도록 설정합니다. 예를 들어, 특정 서비스에 필요한 포트만 허용하고 나머지 포트는 차단합니다. 3. 정기적인 보안 그룹 리뷰를 수행하여 아웃바운드 정책을 엄격하게 관리하고, 필요하지 않은 아웃바운드 트래픽을 차단합니다. 4. 네트워크 ACL을 사용하여 보안 그룹과 별도로 보안 계층을 추가하고 아웃바운드 트래픽을 더욱 세밀하게 제어합니다.',
        'WHERE p.ip_permissions_egress LIKE ''%CidrIp=0.0.0.0/0%'';',
        'SG'
    ),
    (
        '인스턴스 Key-pair 미사용',
        '접근 관리',
        'High',
        'PCI DSS 6.5.10, ISO/IEC 27001:2013 A.9.4, NIST SP 800-53 SC-13, CIS AWS Foundations Benchmark 4.1',
        '인스턴스에 Key-pair를 사용하지 않는 경우, SSH를 통한 보안 연결이 불가능하며, 인스턴스에 대한 접근이 불가능해질 수 있습니다. 이는 보안 및 관리상의 문제가 발생할 수 있습니다.',
        '1. 인스턴스 생성 시 반드시 Key-pair를 생성하여 사용합니다. 이를 통해 SSH를 통한 안전한 접근이 가능해집니다. 2. 기존 인스턴스에 Key-pair를 추가하려면, 새 인스턴스를 생성하고 데이터를 마이그레이션하거나, 기존 인스턴스에 직접 접근할 수 있는 다른 보안 수단을 사용합니다. 3. Key-pair를 안전하게 관리하고 주기적으로 변경하여 보안 수준을 유지합니다. 4. Key-pair 관리 정책을 수립하고 준수하여 인스턴스 접근에 대한 통제력을 높입니다.',
        'WHERE p.key_name='' OR p.key_name IS NULL',
        'EC2'
    ),
    (
        '미사용 ENI 점검',
        '설정 관리',
        'Low',
        'ISO/IEC 27001:2013 A.12.6.1, CIS AWS Foundations Benchmark 2.8',
        '사용하지 않는 Elastic Network Interface (ENI)를 감지하여 불필요한 비용과 리소스 낭비를 줄이는데 도움이 됩니다. ENI는 Amazon VPC 내에서 인스턴스와 네트워크를 연결하는 데 사용됩니다.',
        '사용하지 않는 ENI를 정기적으로 검토하고 제거하세요. AWS Lambda와 Amazon CloudWatch Events를 사용하여 사용하지 않는 ENI를 주기적으로 확인하고 자동으로 삭제하는 자동화된 프로세스를 구축할 수 있습니다.',
        'WHERE status=''available'' AND (attachment='' OR attachment IS NULL) AND (instance_id='' OR instance_id IS NULL)',
        'ENI'
    ),
    (
        '암호화되지 않은 EBS 점검',
        '설정 관리',
        'Low',
        'PCI DSS 3.4, ISO/IEC 27001:2013 A.8.2.3, CIS AWS Foundations Benchmark 3.3',
        '비암호화된 EBS 볼륨은 민감한 데이터가 유출될 위험이 있으므로, 데이터 보호 및 관련 규제 준수를 위한 규정에 위반될 수 있습니다.',
        '모든 EBS 볼륨을 암호화하세요. AWS Key Management Service(KMS)를 사용하여 관리되는 키 또는 고객 관리 키로 EBS 볼륨 암호화를 설정할 수 있습니다. 기존 비암호화된 EBS 볼륨의 경우, 암호화된 스냅샷을 생성한 다음 새 암호화된 볼륨으로 복원하세요.',
        'WHERE p.encrypted=''0'';',
        'EBS'
    ),(
        '미사용 EBS 볼륨 탐지',
        '암호화',
        'Medium',
        'ISO/IEC 27001:2013 A.12.6.1, CIS AWS Foundations Benchmark 2.9',
        '사용하지 않는 Amazon Elastic Block Store (EBS) 볼륨을 탐지하여 비용 및 리소스 낭비를 줄입니다. EBS 볼륨은 EC2 인스턴스에 추가 스토리지를 제공하는 데 사용됩니다.',
        '사용하지 않는 EBS 볼륨을 정기적으로 검토하고 삭제하세요. AWS Lambda와 Amazon CloudWatch Events를 사용하여 사용하지 않는 EBS 볼륨을 주기적으로 확인하고 자동으로 삭제하는 자동화된 프로세스를 구축할 수 있습니다.',
        'WHERE p.state = ''available'' AND p.attachments = ''[]'';',
        'EBS'
    ),
    (
        '서울 리전이 아닌 인스턴스 탐지',
        '설정 관리',
        'Medium',
        'ISO/IEC 27001:2013 A.12.6.1, CIS AWS Foundations Benchmark 2.9 - Ensure that no security groups allow ingress from 0.0.0.0/0 to port 22:',
        '특정 국가의 데이터 주관 법률 및 정책에 따라, 데이터를 국내에 저장해야 하는 경우 서울 리전 외에 인스턴스를 생성하면, 해당 법률 및 정책에 위반될 수 있습니다.',
        '서비스 제어 정책(Service Control Policies, SCP)을 사용하여 서울 리전에서만 인스턴스를 생성하도록 제한하세요. 이를 통해 국내 데이터 주관 법률 및 정책을 준수하게 됩니다.',
        'WHERE p.placement LIKE ''%AvailabilityZone=ap-northeast-2%'';',
     'EC2'
    ),(
        'S3 Public 설정 여부',
        '설정 관리',
        'High',
        'PCI DSS 3.2.1, ISO/IEC 27001:2013 A.8.2.3, CIS AWS Foundations Benchmark 3.1',
        'S3 버킷이 퍼블릭으로 설정되어 있는지 여부를 점검합니다. 퍼블릭으로 설정된 버킷은 누구나 접근할 수 있어 민감한 데이터가 노출될 위험이 있습니다.',
        '1. S3 버킷 정책을 검토하여 불필요한 퍼블릭 접근을 차단합니다. 2. 버킷 ACL(Access Control List)을 확인하고 필요에 따라 수정합니다. 3. AWS Identity and Access Management (IAM) 정책을 사용하여 버킷 접근을 제한합니다. 4. S3 블록 퍼블릭 액세스 기능을 사용하여 버킷 및 객체에 대한 퍼블릭 접근을 방지합니다. 5. 정기적으로 S3 접근 로그를 검토하여 이상 활동을 모니터링합니다.',
        'WHERE p.public_access_block_configuration LIKE ''%BlockPublicAcls=false%'' AND p.public_access_block_configuration LIKE ''%IgnorePublicAcls=false%'' AND p.public_access_block_configuration LIKE ''%BlockPublicPolicy=false%'' AND p.public_access_block_configuration LIKE ''%RestrictPublicBuckets=false%'';',
       'S3'
    ),
    (
        'ENI Public IP 탐지',
        '설정 관리',
        'Medium',
        'ISO/IEC 27001:2013 A.12.1.2, CIS AWS Foundations Benchmark 2.6',
        'ENI (Elastic Network Interface)에 할당된 Public IP를 탐지합니다. Public IP가 할당된 ENI는 인터넷으로부터 접근이 가능하므로, 이를 적절하게 관리하지 않으면 보안 위협에 노출될 수 있습니다.',
        '1. 모든 Public IP 할당을 주기적으로 검토하고 필요한 경우에만 Public IP를 할당합니다. 2. 필요하지 않은 Public IP를 제거하여 불필요한 노출을 최소화합니다. 3. 보안 그룹과 네트워크 ACL을 사용하여 Public IP로의 접근을 엄격하게 제어합니다. 4. AWS Config와 같은 서비스를 사용하여 Public IP 할당 변경 사항을 모니터링하고 알림을 설정합니다.',
        'WHERE NOT ( p.private_ip_address LIKE ''10.%'' OR ( p.private_ip_address LIKE ''172.16.%'' OR p.private_ip_address LIKE ''172.17.%'' OR p.private_ip_address LIKE ''172.18.%'' OR p.private_ip_address LIKE ''172.19.%'' OR p.private_ip_address LIKE ''172.20.%'' OR p.private_ip_address LIKE ''172.21.%'' OR p.private_ip_address LIKE ''172.22.%'' OR p.private_ip_address LIKE ''172.23.%'' OR p.private_ip_address LIKE ''172.24.%'' OR p.private_ip_address LIKE ''172.25.%'' OR p.private_ip_address LIKE ''172.26.%'' OR p.private_ip_address LIKE ''172.27.%'' OR p.private_ip_address LIKE ''172.28.%'' OR p.private_ip_address LIKE ''172.29.%'' OR p.private_ip_address LIKE ''172.30.%'' OR p.private_ip_address LIKE ''172.31.%'') OR p.private_ip_address LIKE ''192.168.%'');',
        'ENI'
    ),(
        '미사용 IGW 탐지',
        '설정 관리',
        'Medium',
        'ISO/IEC 27001:2013 A.12.1.2, CIS AWS Foundations Benchmark 4.3',
        '미사용 상태인 Internet Gateway (IGW)를 감지하여 불필요한 자원 낭비와 보안 위험을 줄이는 데 도움을 줍니다. IGW는 VPC와 인터넷 간의 통신을 가능하게 하는 네트워크 구성 요소로, 사용되지 않는 IGW를 제거하면 리소스 최적화와 보안 강화에 기여할 수 있습니다.',
        '1. 정기적으로 VPC 설정을 검토하여 사용되지 않는 IGW를 식별합니다. 2. 사용되지 않는 IGW를 삭제하여 리소스를 최적화하고 불필요한 비용을 절감합니다. 3. 필요한 경우, 삭제된 IGW 대신 적절한 라우팅 설정을 통해 필요한 트래픽만 허용되도록 합니다. 4. AWS Config나 Lambda 스크립트를 사용하여 미사용 IGW를 자동으로 감지하고 알림을 설정하여 지속적으로 모니터링합니다.',
        'WHERE p.attachments=''[]'';',
       'IGW'
    ),(
        'Instance Public IP 탐지',
        '설정 관리',
        'Low',
        'ISO/IEC 27001:2013 A.13.1.1, CIS AWS Foundations Benchmark 2.6',
        '인스턴스에 할당된 Public IP를 감지하여 네트워크 보안 및 자원 관리에 도움을 줍니다. Public IP가 할당된 인스턴스는 인터넷으로부터 접근이 가능하므로, 이를 적절하게 관리하지 않으면 보안 위협에 노출될 수 있습니다.',
        '1. 모든 Public IP 할당을 주기적으로 검토하고 필요한 경우에만 Public IP를 할당합니다. 2. 필요하지 않은 Public IP를 제거하여 불필요한 노출을 최소화합니다. 3. 보안 그룹과 네트워크 ACL을 사용하여 Public IP로의 접근을 엄격하게 제어합니다. 4. AWS Config와 같은 서비스를 사용하여 Public IP 할당 변경 사항을 모니터링하고 알림을 설정합니다.',
        'WHERE p.public_address!='''';',
       'EC2'
    ),(
        '라우팅 테이블 0.0.0.0 탐지',
        '계정 관리',
        'Low',
        'ISO/IEC 27001:2013 A.13.1.1, CIS AWS Foundations Benchmark 4.3',
        '라우팅 테이블에 0.0.0.0/0 경로가 있는지 감지하여 네트워크 보안을 강화합니다. 0.0.0.0/0 경로는 모든 IP 주소로의 트래픽을 허용하는 경로로, 잘못 구성된 경우 보안 위험을 초래할 수 있습니다.',
        '1. 라우팅 테이블을 주기적으로 검토하여 0.0.0.0/0 경로가 필요한지 확인합니다. 2. 0.0.0.0/0 경로가 필요한 경우, 이를 제한된 IP 주소나 네트워크로 변경하거나, 적절한 보안 그룹과 네트워크 ACL을 설정하여 보호합니다. 3. AWS Config와 같은 서비스를 사용하여 라우팅 테이블 변경 사항을 모니터링하고, 알림을 설정하여 관리합니다. 4. 필요하지 않은 0.0.0.0/0 경로를 삭제하여 불필요한 네트워크 노출을 방지합니다.',
        'DestinationCidrBlock=0.0.0.0/0',
       'ROUTE'
    )ON DUPLICATE KEY UPDATE
                          category = VALUES(category),
                          severity = VALUES(severity),
                          compliance = VALUES(compliance),
                          description = VALUES(description),
                          response = VALUES(response),
                          pattern = VALUES(pattern);


INSERT INTO scan_group (ebs, eni, iam, instance, internet_gate_way, rds, route_table, s3, security_group, subnet, vpc, resource_group_name)
SELECT true, true, true, true, true, true, true, true, true, true, true, 'default'
WHERE NOT EXISTS (SELECT 1 FROM scan_group WHERE resource_group_name = 'default');

INSERT INTO scan_group (ebs, eni, iam, instance, internet_gate_way, rds, route_table, s3, security_group, subnet, vpc, resource_group_name)
SELECT false, false, false, false, true, true, true, true, true, true, true, 'VPC Group'
WHERE NOT EXISTS (SELECT 1 FROM scan_group WHERE resource_group_name = 'VPC Group');

INSERT INTO scan_group (ebs, eni, iam, instance, internet_gate_way, rds, route_table, s3, security_group, subnet, vpc, resource_group_name)
SELECT true, true, false, true, false, false, false, false, false, false, false, '인스턴스 Group'
WHERE NOT EXISTS (SELECT 1 FROM scan_group WHERE resource_group_name = '인스턴스 Group');