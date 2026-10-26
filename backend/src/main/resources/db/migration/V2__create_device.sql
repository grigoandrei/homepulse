CREATE TABLE device (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    person_id bigint not null,
    platform text not null,
    label text not null,
    token_hash text not null unique,
    created_at timestamptz not null default now(),
    last_seen_at timestamptz,
    CONSTRAINT fk_device_person
        FOREIGN KEY(person_id)
            REFERENCES person(id),
    CONSTRAINT chk_device_platform
        CHECK (platform IN ('ANDROID', 'IOS'))
);
CREATE INDEX idx_device_person_id ON device (person_id);