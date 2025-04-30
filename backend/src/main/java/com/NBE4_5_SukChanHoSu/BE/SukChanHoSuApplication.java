package com.NBE4_5_SukChanHoSu.BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SukChanHoSuApplication {

    public static void main(String[] args) {
        SpringApplication.run(SukChanHoSuApplication.class, args);
    }
	@RestController
	public class HelloController {
		@GetMapping("/hello")
		public String hello() {
			return "hello";
		}
	}
}
