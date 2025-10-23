package com.github.renancvitor.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.github.renancvitor.inventory.application.email.service.EmailService;
import com.github.renancvitor.inventory.infra.config.AwsSasConfig;

@SpringBootTest(classes = InventoryNotificationSystemBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class InventoryNotificationSystemBackendApplicationTests {

	@SuppressWarnings("removal")
	@MockBean
	private AwsSasConfig awsSasConfig;

	@SuppressWarnings("removal")
	@MockBean
	private EmailService emailService;

	@Test
	void contextLoads() {
	}

}
