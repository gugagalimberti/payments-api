package com.galimberti.paymentsapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=dGVzdGUtdGVzdGUtdGVzdGUtdGVzdGUtdGVzdGUtMTI=")
class PaymentsApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
