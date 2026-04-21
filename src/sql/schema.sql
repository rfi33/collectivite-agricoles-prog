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
                                id                  VARCHAR(36)  PRIMARY KEY,
                                location            VARCHAR(255) NOT NULL,
                                federation_approval BOOLEAN      NOT NULL DEFAULT FALSE,
                                president_id        VARCHAR(36),
                                vice_president_id   VARCHAR(36),
                                treasurer_id        VARCHAR(36),
                                secretary_id        VARCHAR(36)
);
CREATE TABLE members (
                         id                    VARCHAR(36)       PRIMARY KEY,
                         first_name            VARCHAR(100)      NOT NULL,
                         last_name             VARCHAR(100)      NOT NULL,
                         birth_date            DATE              NOT NULL,
                         gender                gender            NOT NULL,
                         address               VARCHAR(255)      NOT NULL,
                         profession            VARCHAR(100)      NOT NULL,
                         phone_number          VARCHAR(20)       NOT NULL,
                         email                 VARCHAR(150)      NOT NULL,
                         occupation            member_occupation NOT NULL DEFAULT 'JUNIOR',
                         collectivity_id       VARCHAR(36)       REFERENCES collectivities(id),
                         join_date             DATE              NOT NULL DEFAULT CURRENT_DATE,
                         registration_fee_paid BOOLEAN           NOT NULL DEFAULT FALSE,
                         membership_dues_paid  BOOLEAN           NOT NULL DEFAULT FALSE
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
                                      collectivity_id VARCHAR(36) NOT NULL REFERENCES collectivities(id),
                                      member_id       VARCHAR(36) NOT NULL REFERENCES members(id),
                                      PRIMARY KEY (collectivity_id, member_id)
);

CREATE TABLE member_referees (
                                 member_id  VARCHAR(36)  NOT NULL REFERENCES members(id),
                                 referee_id VARCHAR(36)  NOT NULL REFERENCES members(id),
                                 relation   VARCHAR(100) NOT NULL,
                                 PRIMARY KEY (member_id, referee_id),
                                 CONSTRAINT chk_no_self_referee CHECK (member_id <> referee_id)
);