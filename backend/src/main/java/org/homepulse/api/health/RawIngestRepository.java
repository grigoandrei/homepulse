package org.homepulse.api.health;

import org.homepulse.api.people.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawIngestRepository extends JpaRepository<Device, Long> {
    boolean existsByDeviceIdAndSequence(Long deviceId, Long sequence);
}
