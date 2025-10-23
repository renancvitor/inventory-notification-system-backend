package com.github.renancvitor.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.github.renancvitor.inventory.infra.config.AwsSasConfig;

@SpringBootTest(properties = {
		"spring.config.location=classpath:/application-test.properties"
})
@ActiveProfiles("test")
class InventoryNotificationSystemBackendApplicationTests {

	@SuppressWarnings("removal")
	@MockBean
	private AwsSasConfig awsSasConfig;

	@Test
	void contextLoads() {
	}

}
