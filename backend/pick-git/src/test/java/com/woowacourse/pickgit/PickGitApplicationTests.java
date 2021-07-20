package com.woowacourse.pickgit;

import com.woowacourse.pickgit.config.TestInfrastructureConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestInfrastructureConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class PickGitApplicationTests {

	@Test
	void contextLoads() {
	}
}
