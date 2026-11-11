package org.homepulse.api.health;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.homepulse.api.people.Device;

import java.time.Instant;

@Entity
public class RawIngest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id")
    private Device device;

    @Enumerated(EnumType.STRING)
    private IngestStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    private Instant receivedAt;
    private String error;
    private Long sequence;

    protected RawIngest() {}

    public RawIngest(Device device, String payload, Long sequence, Instant receivedAt) {
        this.device = device;
        this.payload = payload;
        this.sequence = sequence;
        this.receivedAt = receivedAt;
        this.status = IngestStatus.RECEIVED;
    }

    public Long getId() {
        return id;
    }

    public Device getDevice() {
        return device;
    }

    public String getPayload() {
        return payload;
    }

    public IngestStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Long getSequence() {
        return sequence;
    }

    public void markParsed() {
        this.status = IngestStatus.PARSED;
        this.error = null;
    }

    public void markFailed(String error) {
        this.status = IngestStatus.FAILED;
        this.error = error;
    }
}
