package com.github.renancvitor.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"spring.config.location=classpath:/application-test.properties"
})
@ActiveProfiles("test")
class InventoryNotificationSystemBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
