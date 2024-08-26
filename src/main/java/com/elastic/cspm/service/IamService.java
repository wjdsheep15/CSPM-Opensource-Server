package com.elastic.cspm.service;

import com.elastic.cspm.data.dto.IamSelectDto;
import com.elastic.cspm.data.entity.IAM;
import com.elastic.cspm.data.repository.IamRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IamService {
    private final IamRepository iamRepository;

    public List<String> getIAMNicknames() {
        return iamRepository.findAll()
                .stream()
                .map(IAM::getNickName)
                .collect(Collectors.toList());
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
