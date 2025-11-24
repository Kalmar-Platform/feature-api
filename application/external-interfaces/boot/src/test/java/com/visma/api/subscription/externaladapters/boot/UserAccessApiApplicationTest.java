package com.visma.api.subscription.externaladapters.boot;

import com.visma.kalmar.api.UserAccessApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = UserAccessApiApplication.class)
@ActiveProfiles("test")
class UserAccessApiApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
    }
}
