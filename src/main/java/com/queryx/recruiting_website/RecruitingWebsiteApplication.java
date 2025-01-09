package com.queryx.recruiting_website;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.queryx.recruiting_website.mapper")
public class RecruitingWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecruitingWebsiteApplication.class, args);
	}

}
