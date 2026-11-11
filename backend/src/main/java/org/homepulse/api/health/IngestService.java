package org.homepulse.api.health;

import org.homepulse.api.people.Device;
import org.homepulse.api.people.DeviceRepository;
import org.springframework.transaction.annotation.Transactional;
import org.homepulse.api.security.DevicePrincipal;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Optional;

@Service
public class IngestService {
    private final RawIngestRepository rawIngests;
    private final DeviceRepository devices;
    private final JsonMapper jsonMapper;

    public IngestService(RawIngestRepository rawIngests, DeviceRepository devices, JsonMapper jsonMapper) {
        this.rawIngests = rawIngests;
        this.devices = devices;
        this.jsonMapper = jsonMapper;
    }

    @Transactional
    public Optional<Long> receive(DevicePrincipal device, String body) {
        JsonNode root = jsonMapper.readTree(body);
        JsonNode node = root.path("sequence");
        Long sequence = (node.isMissingNode() || node.isNull()) ? null : node.asLong();

        if (sequence != null && rawIngests.existsByDeviceIdAndSequence(device.deviceId(), sequence)) {
            return Optional.empty();
        }

        Device ref = devices.getReferenceById(device.deviceId());
        RawIngest saved = rawIngests.save(new RawIngest(ref, body, sequence, Instant.now()));

        return Optional.of(saved.getId());
    }
}
