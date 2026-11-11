package org.homepulse.api.health;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RawIngestRepository extends JpaRepository<RawIngest, Long> {
    boolean existsByDeviceIdAndSequence(Long deviceId, Long sequence);
}
