package com.elastic.cspm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TestContorller {

    @GetMapping("/api/get")
    public Map<String, String> get() {
        return Map.of("name","승근" );
    }
}
