package com.elastic.cspm.service;

import com.elastic.cspm.data.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingRefreshService {

    private final RefreshRepository refreshRepository;

    @Scheduled(cron = "0 0 4 * * ?")
    public void deleteExpiredData() {
        log.info("스케줄링 : 기간 만료된 refreshToken 제거");
        refreshRepository.deleteByExpirationBefore(LocalDateTime.now());
    }
}
