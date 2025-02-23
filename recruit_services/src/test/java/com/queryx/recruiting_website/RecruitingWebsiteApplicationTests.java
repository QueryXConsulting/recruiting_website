package com.queryx.recruiting_website;

import com.queryx.recruiting_website.mapper.InterviewMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RecruitingWebsiteApplicationTests {

	@Autowired
	private InterviewMapper dbMapper;
	@Test
	void contextLoads() {
		System.out.println(dbMapper.selectById(1));
	}

}
