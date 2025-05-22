package com.desireevaldes.miniurl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MiniurlApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniurlApplication.class, args);
	}

}
