package org.homepulse.api.health;

import org.homepulse.api.TestcontainersConfiguration;
import org.homepulse.api.people.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class IngestControllerTest {
    private static final String TOKEN = "test-token";
    private static final String INGEST = "/api/ingest/health";

    @Autowired private MockMvcTester mockMvc;
    @Autowired private PersonRepository people;
    @Autowired private DeviceRepository devices;
    @Autowired RawIngestRepository rawIngests;
    @Autowired private DeviceTokens tokens;

    @BeforeEach
    void saveDevice() {
        Person person = people.save(new Person("Test person"));
        devices.save(new Device(person, Platform.ANDROID, "Test phone", tokens.hash(TOKEN), Instant.now()));
    }

    @AfterEach
    void cleanUp() {
        rawIngests.deleteAll();
        devices.deleteAll();
        people.deleteAll();
    }

    @Test
    void validPayloadIsSavedAsReceived() {
        String body = "{\"sequence\": 1}";

        MvcTestResult result = mockMvc.post().uri(INGEST)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.ACCEPTED);
        assertThat(rawIngests.findAll())
                .singleElement()
                .satisfies(raw -> {
                    assertThat(raw.getStatus()).isEqualTo(IngestStatus.RECEIVED);
                    assertThat(raw.getPayload()).contains("\"sequence\"");
                });
    }

    @Test
    void invalidJsonIsRejectedAndNotSaved() {
        MvcTestResult result = post("not json");

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(rawIngests.count()).isZero();
    }

    @Test
    void payloadsWithoutSequenceAreAllSaved() {
        post("{}");
        post("{}");

        assertThat(rawIngests.findAll())
                .hasSize(2)
                .allSatisfy(raw -> assertThat(raw.getSequence()).isNull());
    }

    @Test
    void resendWithSameSequenceIsSavedOnce() {
        MvcTestResult post1 = post("{\"sequence\": 7}");
        MvcTestResult post2 = post("{\"sequence\": 7}");

        assertThat(post1).hasStatus(HttpStatus.ACCEPTED);
        assertThat(post2).hasStatus(HttpStatus.ACCEPTED);
        assertThat(rawIngests.count()).isEqualTo(1);
    }

    private MvcTestResult post(String body) {
        return mockMvc.post().uri(INGEST)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }
}
