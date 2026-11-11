package org.homepulse.api.security;

import org.homepulse.api.TestcontainersConfiguration;
import org.homepulse.api.health.RawIngestRepository;
import org.homepulse.api.people.Device;
import org.homepulse.api.people.DeviceRepository;
import org.homepulse.api.people.DeviceTokens;
import org.homepulse.api.people.Person;
import org.homepulse.api.people.PersonRepository;
import org.homepulse.api.people.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class IngestSecurityTest {

    private static final String TOKEN = "test-token";
    private static final String INGEST = "/api/ingest/health";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private PersonRepository people;

    @Autowired
    private DeviceRepository devices;

    @Autowired
    private DeviceTokens tokens;

    @Autowired
    private RawIngestRepository rawIngests;

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
    void ingestWithoutTokenIsUnauthorized() {
        assertThat(mvc.post().uri(INGEST).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void ingestWithWrongTokenIsUnauthorized() {
        assertThat(mvc.post().uri(INGEST)
                .header(HttpHeaders.AUTHORIZATION, "Bearer wrong")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void ingestWithValidTokenIsAccepted() {
        assertThat(mvc.post().uri(INGEST)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .hasStatus(HttpStatus.ACCEPTED);
    }

    @Test
    void validTokenMarksDeviceSeen() {
        mvc.post().uri(INGEST)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content("{}")
                .exchange();

        assertThat(devices.findByTokenHash(tokens.hash(TOKEN)))
                .hasValueSatisfying(device -> assertThat(device.getLastSeenAt()).isNotNull());
    }

    @Test
    void pingIsOpenWithoutToken() {
        assertThat(mvc.get().uri("/api/ping")).hasStatusOk();
    }

    @Test
    void unknownPathIsUnauthorizedWithoutToken() {
        assertThat(mvc.get().uri("/api/anything")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void unknownPathIsForbiddenEvenWithValidToken() {
        assertThat(mvc.get().uri("/api/anything")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .hasStatus(HttpStatus.FORBIDDEN);
    }
}
