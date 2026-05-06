CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');

CREATE TYPE member_occupation AS ENUM (
    'JUNIOR', 'CONFIRMED', 'SENIOR',
    'SECRETARY', 'TREASURER', 'VICE_PRESIDENT', 'PRESIDENT'
);

CREATE TYPE frequency AS ENUM (
    'WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY'
);

CREATE TYPE activity_status AS ENUM ('ACTIVE', 'INACTIVE');

CREATE TYPE mobile_banking_service AS ENUM (
    'AIRTEL_MONEY', 'MVOLA', 'ORANGE_MONEY'
);

CREATE TYPE bank_name_enum AS ENUM (
    'BRED', 'MCB', 'BMOI', 'BOA', 'BGFI',
    'AFG', 'ACCES_BAQUE', 'BAOBAB', 'SIPEM'
);

CREATE TYPE payment_mode AS ENUM (
    'CASH', 'MOBILE_BANKING', 'BANK_TRANSFER'
);

CREATE TYPE transaction_type AS ENUM ('IN', 'OUT');

CREATE TYPE specialization AS ENUM (
    'RIZICULTURE', 'PISCICULTURE', 'APICULTURE',
    'AGRICULTURE_GENERALE', 'ELEVAGE', 'MARAICHAGE'
);

CREATE TABLE "collectivity" (
                                id                  VARCHAR(36)    PRIMARY KEY,
                                name                VARCHAR(255)   UNIQUE,
                                number              INTEGER        UNIQUE,
                                location            VARCHAR(255)   NOT NULL,
                                specialization      specialization,
                                federation_approval BOOLEAN        NOT NULL DEFAULT FALSE,
                                creation_date       DATE           NOT NULL DEFAULT CURRENT_DATE,
                                president_id        VARCHAR(36),
                                vice_president_id   VARCHAR(36),
                                treasurer_id        VARCHAR(36),
                                secretary_id        VARCHAR(36)
);

CREATE TABLE "member" (
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
                          join_date             DATE              NOT NULL DEFAULT CURRENT_DATE,
                          registration_fee_paid BOOLEAN           NOT NULL DEFAULT FALSE,
                          membership_dues_paid  BOOLEAN           NOT NULL DEFAULT FALSE
);

ALTER TABLE "collectivity"
    ADD CONSTRAINT fk_president      FOREIGN KEY (president_id)      REFERENCES "member"(id),
    ADD CONSTRAINT fk_vice_president FOREIGN KEY (vice_president_id) REFERENCES "member"(id),
    ADD CONSTRAINT fk_treasurer      FOREIGN KEY (treasurer_id)      REFERENCES "member"(id),
    ADD CONSTRAINT fk_secretary      FOREIGN KEY (secretary_id)      REFERENCES "member"(id);

CREATE TABLE collectivity_member (
                                     id              VARCHAR(36) PRIMARY KEY,
                                     member_id       VARCHAR(36) NOT NULL REFERENCES "member"(id),
                                     collectivity_id VARCHAR(36) NOT NULL REFERENCES "collectivity"(id),
                                     join_date       DATE        NOT NULL DEFAULT CURRENT_DATE,
                                     UNIQUE (member_id, collectivity_id)
);

CREATE TABLE member_referee (
                                id                 VARCHAR(36) PRIMARY KEY,
                                member_referee_id  VARCHAR(36) NOT NULL REFERENCES "member"(id),
                                member_refereed_id VARCHAR(36) NOT NULL REFERENCES "member"(id),
                                UNIQUE (member_referee_id, member_refereed_id),
                                CONSTRAINT chk_no_self_referee CHECK (member_referee_id <> member_refereed_id)
);

CREATE TABLE membership_fee (
                                id              VARCHAR(50)     PRIMARY KEY,
                                collectivity_id VARCHAR(50)     NOT NULL REFERENCES "collectivity"(id),
                                eligible_from   DATE            NOT NULL,
                                frequency       frequency       NOT NULL,
                                amount          NUMERIC(15, 2)  NOT NULL CHECK (amount >= 0),
                                label           VARCHAR(255),
                                status          activity_status NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE cash_account (
                              id              VARCHAR(50) PRIMARY KEY,
                              collectivity_id VARCHAR(50) NOT NULL UNIQUE REFERENCES "collectivity"(id)
);

CREATE TABLE bank_account (
                              id              VARCHAR(50)   PRIMARY KEY,
                              collectivity_id VARCHAR(50)   NOT NULL REFERENCES "collectivity"(id),
                              holder_name     VARCHAR(255),
                              bank_name       bank_name_enum,
                              bank_code       INTEGER,
                              branch_code     INTEGER,
                              account_number  BIGINT,
                              key             INTEGER
);

CREATE TABLE mobile_banking_account (
                                        id              VARCHAR(50)            PRIMARY KEY,
                                        collectivity_id VARCHAR(50)            NOT NULL REFERENCES "collectivity"(id),
                                        holder_name     VARCHAR(255),
                                        service         mobile_banking_service,
                                        mobile_number   VARCHAR(20)
);

CREATE TABLE "transaction" (
                               id                   VARCHAR(50)      PRIMARY KEY,
                               financial_account_id VARCHAR(50)      NOT NULL,
                               amount               NUMERIC(15, 2)   NOT NULL,
                               creation_date        DATE             NOT NULL DEFAULT CURRENT_DATE,
                               transaction_type     transaction_type NOT NULL
);

CREATE TABLE collectivities_transactions (
                                             id                  VARCHAR(50)    PRIMARY KEY,
                                             creation_date       DATE           NOT NULL DEFAULT CURRENT_DATE,
                                             amount              DECIMAL(15, 2) NOT NULL,
                                             collectivity_id     VARCHAR(50)    NOT NULL REFERENCES "collectivity"(id),
                                             member_id           VARCHAR(50)    NOT NULL REFERENCES "member"(id),
                                             account_credited_id VARCHAR(50)    NOT NULL,
                                             payment_mode        payment_mode   NOT NULL
);