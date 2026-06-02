CREATE TABLE IF NOT EXISTS courses (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    code         VARCHAR(20)  NOT NULL UNIQUE,
    credits      INT          NOT NULL,
    max_students INT          NOT NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    professor_id BIGINT       NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_courses_code ON courses (code);
CREATE INDEX idx_courses_professor ON courses (professor_id);
CREATE INDEX idx_courses_status ON courses (status);
