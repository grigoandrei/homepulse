package org.homepulse.api.people;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    private Person person;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String label;
    private String tokenHash;
    private Instant createdAt;
    private Instant lastSeenAt;

    protected Device() {
    }

    public Device(Person person, Platform platform, String label, String tokenHash, Instant createdAt) {
        this.person = person;
        this.platform = platform;
        this.label = label;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public Platform getPlatform() {
        return platform;
    }

    public String getLabel() {
        return label;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void markSeen(Instant now) {
        this.lastSeenAt = now;
    }
}
