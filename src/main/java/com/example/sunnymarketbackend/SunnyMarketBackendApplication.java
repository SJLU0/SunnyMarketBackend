package com.example.sunnymarketbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.sunnymarketbackend.dao")
public class SunnyMarketBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunnyMarketBackendApplication.class, args);
	}

}
