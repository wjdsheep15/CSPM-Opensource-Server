package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.GraphScanDto;
import com.elastic.cspm.data.dto.ResponseScanGroupDto;
import com.elastic.cspm.data.dto.ScanGroupDto;
import com.elastic.cspm.data.entity.BridgeEntity;
import com.elastic.cspm.data.entity.DescribeResult;
import com.elastic.cspm.data.entity.Member;
import com.elastic.cspm.data.entity.ScanGroup;
import com.elastic.cspm.data.repository.BridgeEntityRepository;
import com.elastic.cspm.data.repository.DescribeResultRepository;
import com.elastic.cspm.data.repository.MemberRepository;
import com.elastic.cspm.data.repository.ScanGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final ScanGroupRepository scanGroupRepository;
    private final BridgeEntityRepository bridgeEntityRepository;
    private final DescribeResultRepository describeResultRepository;

    public List<ScanGroupDto> getScanGroup(String email){

       List<BridgeEntity> bridgeEntityList = bridgeEntityRepository.findAllByMemberEmail(email).orElseThrow();
       if(bridgeEntityList.isEmpty()){
           return new ArrayList<>();
       }

        List<ScanGroup> scanGroupStream = bridgeEntityList.stream()
                .map(bridgeEntity -> scanGroupRepository.findByResourceGroupName(bridgeEntity.getScanGroup().getResourceGroupName())
                        .orElse(null))
                .filter(scanGroup -> scanGroup != null)
                .toList();

        List<ScanGroupDto> scanGroupDtos = scanGroupStream.stream().map(ScanGroupDto::of).toList();

        return scanGroupDtos;
    }

    @Transactional
    public Boolean saveGroup(String email, ResponseScanGroupDto responseScanGroupDto) {

        ScanGroup scanGroup = new ScanGroup();
        String afterResourceGroupName = responseScanGroupDto.getAfterResourceGroupName();
        String beforeResourceGroupName = responseScanGroupDto.getBeforeResourceGroupName();

        // 새로운 그룹 저장하기
        if (responseScanGroupDto.getUpdateNumber() != -1) {
            return false;
        }
            log.info("새로운 그룹 저장히가");
            scanGroup.setResourceGroupName(afterResourceGroupName);
            scanGroup.setVpc(responseScanGroupDto.getVpc());
            scanGroup.setSubnet(responseScanGroupDto.getSubnet());
            scanGroup.setRouteTable(responseScanGroupDto.getRouteTable());
            scanGroup.setInternetGateway(responseScanGroupDto.getInternetGateway());
            scanGroup.setInstance(responseScanGroupDto.getInstance());
            scanGroup.setEni(responseScanGroupDto.getEni());
            scanGroup.setEbs(responseScanGroupDto.getEbs());
            scanGroup.setS3(responseScanGroupDto.getS3());
            scanGroup.setSecurityGroup(responseScanGroupDto.getSecurityGroup());
            scanGroup.setIam(responseScanGroupDto.getIam());
            scanGroup.setRds(responseScanGroupDto.getRds());

            scanGroupRepository.save(scanGroup);
            Boolean bridgeResout = populateScanGroup(email, afterResourceGroupName);
            return bridgeResout;


    }

    private boolean populateScanGroup(String email, String afterResourceGroupName) {
        log.info("bridge 저장하기");
        Member member = memberRepository.findByEmail(email).orElse(null);
        ScanGroup scangroup = scanGroupRepository.findByResourceGroupName(afterResourceGroupName).orElse(null);

        if(member == null && scangroup == null){
            return false;
        }
        BridgeEntity bridgeEntity = new BridgeEntity();
        bridgeEntity.setMember(member);
        bridgeEntity.setScanGroup(scangroup);
        bridgeEntityRepository.save(bridgeEntity);
        return true;

    }

    public boolean deleteGroup(String groupName) {
        ScanGroup scanGroup = scanGroupRepository.findByResourceGroupName(groupName).orElse(null);
        if(scanGroup == null){
            return false;
        }
        log.info("삭제 성공");
        scanGroupRepository.delete(scanGroup);
        return true;
    }


    public List<GraphScanDto> getScanGraphData(String groupName) {

        ScanGroup scanGroup = scanGroupRepository.findByResourceGroupName(groupName).orElse(null);
        if(scanGroup == null){
            return null;
        }

        DescribeResult describeResult =  describeResultRepository.findTopByScanGroupOrderByIdDesc(groupName).orElse(null);
        if(describeResult == null){
           return null;
        }

        LocalDateTime lastScanTime = describeResult.getScanTime();
        List<DescribeResult> describeResultList = describeResultRepository.findAllByScanGroupAndScanTime(groupName, lastScanTime).orElse(null);
        if(describeResultList == null){
            return null;
        };

        Map<String, Integer> groupCount = new HashMap<>();
        List<GraphScanDto> graphScanDtosList = new ArrayList<>();

        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("VPC", "hsl(50, 70%, 50%)");
        colorMap.put("Subnet", "hsl(100, 70%, 50%");
        colorMap.put("RouteTable", "hsl(150, 70%, 50%)");
        colorMap.put("InternetGateway", "hsl(200, 70%, 50%)");
        colorMap.put("Instance", "hsl(250, 70%, 50%)");
        colorMap.put("EBS", "hsl(300, 70%, 50%)");
        colorMap.put("S3", "hsl(50, 20%, 70%)");
        colorMap.put("SecurityGroup", "hsl(150, 20%, 70%");
        colorMap.put("IAM", "hsl(250, 20%, 70%)");
        colorMap.put("RDS", "hsl(200, 20%, 70%)");
        colorMap.put("ENI", "hsl(300, 20%, 70%)");

        describeResultList.forEach(entity -> countScanGraphData(groupCount, entity));
        groupCount.forEach((key, value)-> {
            GraphScanDto graphScanDto = new GraphScanDto();
            graphScanDto.setCategory(key);
            graphScanDto.setCount(value);
            graphScanDto.setCountColor(colorMap.getOrDefault(key, "hsl(0, 0%, 50%)"));
            graphScanDtosList.add(graphScanDto);
        });

        return graphScanDtosList;
    }

    public void countScanGraphData(Map<String, Integer> groupCount, DescribeResult describeResult) {

        /**
         * task: getScanGroup이 아닌 resource로 변경
         */
      String resource = describeResult.getScanGroup();
      if(groupCount.containsKey(resource)){
         int count =  groupCount.get(resource);
         groupCount.put(resource, ++count);
      }else{
          groupCount.put(resource, 1);
      }

    }

}
