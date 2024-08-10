package com.elastic.cspm;

import com.elastic.cspm.utils.AES256;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaAuditing
public class CspmApplication {

	private final AES256 aes256Util;

    public static void main(String[] args) {
		SpringApplication.run(CspmApplication.class, args);
	}

	@PostConstruct
	private void aes256Init() { aes256Util.init(); }

}
