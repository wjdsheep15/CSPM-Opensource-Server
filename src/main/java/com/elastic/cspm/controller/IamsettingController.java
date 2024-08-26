package com.elastic.cspm.controller;

import com.elastic.cspm.data.dto.IamSelectDto;
import com.elastic.cspm.service.IamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/iamsettings")
@RequiredArgsConstructor
public class IamsettingController {
    private final IamService iamService;

    @DeleteMapping
    public ResponseEntity<Void> deleteIam(@RequestBody List<IamSelectDto> iamSelectDtoList){
        return iamService.iamDelete(iamSelectDtoList);
    }

}
