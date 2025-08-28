package com.dbProject.Church;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource("classpath:application-test.properties")
class ChurchApplicationTests {

	@Test
	void contextLoads() {
		// Test passes if application context loads successfully
	}

}
