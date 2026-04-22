CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');

CREATE TYPE member_occupation AS ENUM (
    'JUNIOR',
    'SENIOR',
    'SECRETARY',
    'TREASURER',
    'VICE_PRESIDENT',
    'PRESIDENT'
);

CREATE TABLE collectivities (
                                id                  SERIAL PRIMARY KEY,
                                location            VARCHAR(255) NOT NULL,
                                federation_approval BOOLEAN      NOT NULL DEFAULT FALSE,
                                president_id        INTEGER,
                                vice_president_id   INTEGER,
                                treasurer_id        INTEGER,
                                secretary_id        INTEGER
);
CREATE TABLE members (
                         id                    SERIAL PRIMARY KEY,
                         first_name            VARCHAR(100) NOT NULL,
                         last_name             VARCHAR(100) NOT NULL,
                         birth_date            DATE         NOT NULL,
                         gender                gender,
                         address               VARCHAR(255),
                         profession            VARCHAR(100),
                         phone_number          VARCHAR(20),
                         email                 VARCHAR(150),
                         occupation            member_occupation NOT NULL DEFAULT 'JUNIOR',
                         collectivity_id       INTEGER REFERENCES collectivities(id),
                         registration_fee_paid BOOLEAN NOT NULL DEFAULT FALSE,
                         membership_dues_paid  BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE collectivities
    ADD CONSTRAINT fk_president
        FOREIGN KEY (president_id)      REFERENCES members(id),
    ADD CONSTRAINT fk_vice_president
        FOREIGN KEY (vice_president_id) REFERENCES members(id),
    ADD CONSTRAINT fk_treasurer
        FOREIGN KEY (treasurer_id)      REFERENCES members(id),
    ADD CONSTRAINT fk_secretary
        FOREIGN KEY (secretary_id)      REFERENCES members(id);

CREATE TABLE collectivity_members (
                                      collectivity_id INTEGER NOT NULL REFERENCES collectivities(id),
                                      member_id       INTEGER NOT NULL REFERENCES members(id),
                                      PRIMARY KEY (collectivity_id, member_id)
);

CREATE TABLE member_referees (
                                 member_id       INTEGER      NOT NULL REFERENCES members(id),
                                 referee_id      INTEGER      NOT NULL REFERENCES members(id),
                                 relation        VARCHAR(100) NOT NULL,
                                 PRIMARY KEY (member_id, referee_id),
                                 CONSTRAINT chk_no_self_referee CHECK (member_id <> referee_id)
);