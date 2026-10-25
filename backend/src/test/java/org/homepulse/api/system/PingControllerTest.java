package org.homepulse.api.system;

import org.homepulse.api.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class PingControllerTest {
    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void getPingEndpoint() {
        Instant before = Instant.now();
        MvcTestResult result = mockMvcTester.get().uri("/api/ping").exchange();
        Instant after = Instant.now();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.status")
                .isEqualTo("ok");

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.time")
                .asString()
                .satisfies(time -> assertThat(Instant.parse(time)).isBetween(before, after));
    }
}
