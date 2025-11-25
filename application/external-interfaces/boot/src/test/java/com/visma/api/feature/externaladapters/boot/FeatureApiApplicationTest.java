package com.visma.api.feature.externaladapters.boot;

import com.visma.kalmar.api.FeatureApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = FeatureApiApplication.class)
@ActiveProfiles("test")
class FeatureApiApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
    }
}
