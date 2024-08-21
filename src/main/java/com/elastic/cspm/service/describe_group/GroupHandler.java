/*
package com.elastic.cspm.service.describe_group;

import com.elastic.cspm.utils.DescribeType;
import com.elastic.cspm.utils.ResultType;
import com.mysema.commons.lang.Pair;
import com.elastic.cspm.data.dto.DescribeIamDto;
import com.elastic.cspm.data.entity.DescribeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class GroupHandler {


    // 자원 스캔
    public Pair<Boolean, List<DescribeResult>> serviceGroupDescribe(DescribeIamDto describeIamDto) {
        List<Supplier<Pair<Boolean, List<DescribeResult>>>> taskList = new ArrayList<>();

        for (DescribeType.Group group : DescribeType.Group.values()) {
            switch (group) {
                case VPC -> taskList.addAll();
                case EC2 -> taskList.addAll();
                case S3 -> taskList.addAll();
            }
        }
    }

    private Pair<Boolean, List<DescribeResult>> syncTaskRun(List<Supplier<Pair<Boolean, List<DescribeResult>>> tasks) {
        boolean allSuccess = true;
        List<DescribeResult> resultList = new ArrayList<>();

        for (Supplier<Pair<Boolean, List<DescribeResult>>> task : tasks) {
            // 동기적으로 작업 수행
            Pair<Boolean, List<DescribeResult>> pairResult = task.get();

            // 성공 여부 업데이트
            allSuccess = allSuccess && pairResult.getFirst();
        }

        return Pair.of(allSuccess, resultList);
    }

    // 자원 스캔 실행
    public <T> Pair<Boolean, List<T>> safeDescribeCall(DescribeType describeType, Supplier<List<T>> describeCall) {
        boolean isSuccess = true;
        List<T> resultData = Collections.emptyList();
        try {
            resultData = describeCall.get();
        } catch (Ec2Exception | S3Exception e) {
            isSuccess = false;
            e.fillInStackTrace();
        }

        return new Pair<>(isSuccess, resultData);
    }

    private List<DescribeResult> performTask(DescribeResult task) {
        return List.of(task); // 예시로 빈 리스트 반환
    }
}
*/