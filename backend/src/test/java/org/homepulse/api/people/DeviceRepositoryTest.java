package org.homepulse.api.people;

import org.homepulse.api.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class DeviceRepositoryTest {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void findsDeviceByTokenHash() {
        Person andrei = personRepository.save(new Person("Andrei"));
        deviceRepository.save(new Device(andrei, Platform.ANDROID, "Galaxy", "abc123", Instant.now()));

        Optional<Device> found = deviceRepository.findByTokenHash("abc123");

        assertThat(found).hasValueSatisfying(device -> {
            assertThat(device.getLabel()).isEqualTo("Galaxy");
            assertThat(device.getPlatform()).isEqualTo(Platform.ANDROID);
        });
    }

    @Test
    void returnsEmptyForUnknownHash() {
        Optional<Device> found = deviceRepository.findByTokenHash("abc123");
        assertThat(found).isEmpty();
    }
}
