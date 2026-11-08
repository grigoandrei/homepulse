CREATE table raw_ingest (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    device_id bigint not null,
    received_at timestamptz not null,
    sequence bigint,
    payload jsonb not null,
    status text not null,
    error text,

    CONSTRAINT uq_raw_ingest_device_sequence UNIQUE (device_id, sequence),

    CONSTRAINT fk_raw_ingest_device
        FOREIGN KEY(device_id)
            REFERENCES device(id),

    CONSTRAINT chk_raw_ingest_status
        CHECK (status in ('RECEIVED', 'PARSED', 'FAILED'))

);