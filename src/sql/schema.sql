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

-- Correction : number en INTEGER (était VARCHAR dans la version originale)
ALTER TABLE collectivities
    ADD COLUMN name   VARCHAR(255) UNIQUE,
    ADD COLUMN number INTEGER      UNIQUE;

CREATE TYPE frequency AS ENUM (
    'WEEKLY',
    'MONTHLY',
    'ANNUALLY',
    'PUNCTUALLY'
);

CREATE TYPE activity_status AS ENUM (
    'ACTIVE',
    'INACTIVE'
);

CREATE TABLE membership_fees (
                                 id              VARCHAR(50)     PRIMARY KEY,
                                 collectivity_id VARCHAR(50)     NOT NULL REFERENCES collectivities(id),
                                 eligible_from   DATE            NOT NULL,
                                 frequency       frequency       NOT NULL,
                                 amount          NUMERIC(15, 2)  NOT NULL CHECK (amount >= 0),
                                 label           VARCHAR(255),
                                 status          activity_status NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE collectivities_transactions (
                                             id                  VARCHAR(50)    PRIMARY KEY,
                                             creation_date       DATE           NOT NULL,
                                             amount              DECIMAL(15, 2) NOT NULL,
                                             collectivity_id     VARCHAR(50)    NOT NULL,
                                             member_id           VARCHAR(50)    NOT NULL,
                                             account_credited_id VARCHAR(50)    NOT NULL,
                                             payment_mode        VARCHAR(20)    NOT NULL
);

CREATE TYPE account_type AS ENUM (
    'CASH',
    'MOBILE_BANKING',
    'BANK'
);

-- Correction : type renommé mobile_money (cohérent avec la colonne et le code Java)
CREATE TYPE mobile_money AS ENUM (
    'AIRTEL_MONEY',
    'MVOLA',
    'ORANGE_MONEY'
);

CREATE TYPE bank_name AS ENUM (
    'BRED',
    'MCB',
    'BMOI',
    'BOA',
    'BGFI',
    'AFG',
    'ACCES_BAQUE',
    'BAOBAB',
    'SIPEM'
);

CREATE TABLE financial_accounts (
                                    id                  VARCHAR(50)    PRIMARY KEY,
                                    collectivity_id     VARCHAR(50)    NOT NULL REFERENCES collectivities(id),
                                    account_type        account_type   NOT NULL,
                                    amount              NUMERIC(15, 2) NOT NULL DEFAULT 0,
                                    holder_name         VARCHAR(255),
                                    bank_name           bank_name,
                                    bank_code           INTEGER,
                                    bank_branch_code    INTEGER,
                                    bank_account_number BIGINT,
                                    bank_account_key    INTEGER,
                                    mobile_money        mobile_money,
                                    mobile_number       BIGINT
);

CREATE UNIQUE INDEX idx_one_cash_per_collectivity
    ON financial_accounts (collectivity_id)
    WHERE account_type = 'CASH';
