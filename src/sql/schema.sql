CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE'
);

CREATE TYPE member_occupation AS ENUM (
    'JUNIOR',
    'CONFIRMED',
    'SENIOR',
    'SECRETARY',
    'TREASURER',
    'VICE_PRESIDENT',
    'PRESIDENT'
);

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

CREATE TYPE account_type AS ENUM (
    'CASH',
    'MOBILE_BANKING',
    'BANK'
);

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

CREATE TYPE specialization AS ENUM (
    'RIZICULTURE',
    'PISCICULTURE',
    'APICULTURE',
    'AGRICULTURE_GENERALE',
    'ELEVAGE',
    'MARAICHAGE'
);

CREATE TYPE payment_mode AS ENUM (
    'CASH',
    'MOBILE_MONEY',
    'BANK_TRANSFER'
);


CREATE TABLE collectivities (
                                id                  VARCHAR(36)     PRIMARY KEY,
                                name                VARCHAR(255)    UNIQUE,
                                number              INTEGER         UNIQUE,
                                location            VARCHAR(255)    NOT NULL,
                                specialization      specialization,
                                federation_approval BOOLEAN         NOT NULL DEFAULT FALSE,
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

CREATE TABLE membership_fees (
                                 id              VARCHAR(50)     PRIMARY KEY,
                                 collectivity_id VARCHAR(50)     NOT NULL REFERENCES collectivities(id),
                                 eligible_from   DATE            NOT NULL,
                                 frequency       frequency       NOT NULL,
                                 amount          NUMERIC(15, 2)  NOT NULL CHECK (amount >= 0),
                                 label           VARCHAR(255),
                                 status          activity_status NOT NULL DEFAULT 'ACTIVE'
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

CREATE TABLE collectivities_transactions (
                                             id                  VARCHAR(50)    PRIMARY KEY,
                                             creation_date       DATE           NOT NULL,
                                             amount              DECIMAL(15, 2) NOT NULL,
                                             collectivity_id     VARCHAR(50)    NOT NULL REFERENCES collectivities(id),
                                             member_id           VARCHAR(50)    NOT NULL REFERENCES members(id),
                                             account_credited_id VARCHAR(50)    NOT NULL REFERENCES financial_accounts(id),
                                             payment_mode        VARCHAR(20)    NOT NULL
);


INSERT INTO collectivities (id, number, name, location, specialization, federation_approval) VALUES
                                                                                                 ('col-1', 1, 'Mpanorina',      'Ambatondrazaka', 'RIZICULTURE',  TRUE),
                                                                                                 ('col-2', 2, 'Dobo voalohany', 'Ambatondrazaka', 'PISCICULTURE', TRUE),
                                                                                                 ('col-3', 3, 'Tantely mamy',   'Brickaville',    'APICULTURE',   TRUE);

INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, registration_fee_paid, membership_dues_paid) VALUES
                                                                                                                                                                                            ('C1-M1', 'Prénom membre 1',  'Nom membre 1',  '1980-02-01', 'MALE',   'Lot II V M Ambato.',   'Riziculteur',  '0341234567', 'member.1@fed-agri.mg', 'PRESIDENT',      'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M2', 'Prénom membre 2',  'Nom membre 2',  '1982-03-05', 'MALE',   'Lot II F Ambato.',     'Agriculteur',  '0321234567', 'member.2@fed-agri.mg', 'VICE_PRESIDENT', 'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M3', 'Prénom membre 3',  'Nom membre 3',  '1992-03-10', 'MALE',   'Lot II J Ambato.',     'Collecteur',   '0331234567', 'member.3@fed-agri.mg', 'SECRETARY',      'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M4', 'Prénom membre 4',  'Nom membre 4',  '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato.',   'Distributeur', '0381234567', 'member.4@fed-agri.mg', 'TREASURER',      'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M5', 'Prénom membre 5',  'Nom membre 5',  '1999-08-21', 'MALE',   'Lot UV 80 Ambato.',    'Riziculteur',  '0373434567', 'member.5@fed-agri.mg', 'CONFIRMED',      'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M6', 'Prénom membre 6',  'Nom membre 6',  '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato.',     'Riziculteur',  '0372234567', 'member.6@fed-agri.mg', 'CONFIRMED',      'col-1', TRUE, TRUE),
                                                                                                                                                                                            ('C1-M7', 'Prénom membre 7',  'Nom membre 7',  '1998-01-31', 'MALE',   'Lot UV 7 Ambato.',     'Riziculteur',  '0374234567', 'member.7@fed-agri.mg', 'CONFIRMED',      'col-1', TRUE, FALSE),
                                                                                                                                                                                            ('C1-M8', 'Prénom membre 8',  'Nom membre 8',  '1975-08-20', 'MALE',   'Lot UV 8 Ambato.',     'Riziculteur',  '0370234567', 'member.8@fed-agri.mg', 'CONFIRMED',      'col-1', TRUE, FALSE);

INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, registration_fee_paid, membership_dues_paid) VALUES
                                                                                                                                                                                            ('C3-M1', 'Prénom membre 9',  'Nom membre 9',  '1988-01-02', 'MALE',   'Lot 33 J Antsirabe',   'Apiculteur',   '034034567',  'member.9@fed-agri.mg',  'PRESIDENT',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M2', 'Prénom membre 10', 'Nom membre 10', '1982-03-05', 'MALE',   'Lot 2 J Antsirabe',    'Agriculteur',  '0338634567', 'member.10@fed-agri.mg', 'VICE_PRESIDENT', 'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M3', 'Prénom membre 11', 'Nom membre 11', '1992-03-12', 'MALE',   'Lot 8 KM Antsirabe',   'Collecteur',   '0338234567', 'member.11@fed-agri.mg', 'SECRETARY',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M4', 'Prénom membre 12', 'Nom membre 12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'member.12@fed-agri.mg', 'TREASURER',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M5', 'Prénom membre 13', 'Nom membre 13', '1999-08-11', 'MALE',   'Lot UV 80 Antsirabe',  'Apiculteur',   '0373365567', 'member.13@fed-agri.mg', 'CONFIRMED',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M6', 'Prénom membre 14', 'Nom membre 14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe',   'Apiculteur',   '0378234567', 'member.14@fed-agri.mg', 'CONFIRMED',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M7', 'Prénom membre 15', 'Nom membre 15', '1998-01-13', 'MALE',   'Lot UV 7 Antsirabe',   'Apiculteur',   '0374914567', 'member.15@fed-agri.mg', 'CONFIRMED',      'col-3', TRUE, TRUE),
                                                                                                                                                                                            ('C3-M8', 'Prénom membre 16', 'Nom membre 16', '1975-08-02', 'MALE',   'Lot UV 8 Antsirabe',   'Apiculteur',   '0370634567', 'member.16@fed-agri.mg', 'CONFIRMED',      'col-3', TRUE, TRUE);



UPDATE collectivities SET
                          president_id      = 'C1-M1',
                          vice_president_id = 'C1-M2',
                          secretary_id      = 'C1-M3',
                          treasurer_id      = 'C1-M4'
WHERE id = 'col-1';

UPDATE collectivities SET
                          president_id      = 'C1-M5',
                          vice_president_id = 'C1-M6',
                          secretary_id      = 'C1-M7',
                          treasurer_id      = 'C1-M8'
WHERE id = 'col-2';

UPDATE collectivities SET
                          president_id      = 'C3-M1',
                          vice_president_id = 'C3-M2',
                          secretary_id      = 'C3-M3',
                          treasurer_id      = 'C3-M4'
WHERE id = 'col-3';


INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
                                                                  ('col-1', 'C1-M1'), ('col-1', 'C1-M2'), ('col-1', 'C1-M3'), ('col-1', 'C1-M4'),
                                                                  ('col-1', 'C1-M5'), ('col-1', 'C1-M6'), ('col-1', 'C1-M7'), ('col-1', 'C1-M8'),
                                                                  ('col-2', 'C1-M1'), ('col-2', 'C1-M2'), ('col-2', 'C1-M3'), ('col-2', 'C1-M4'),
                                                                  ('col-2', 'C1-M5'), ('col-2', 'C1-M6'), ('col-2', 'C1-M7'), ('col-2', 'C1-M8'),
                                                                  ('col-3', 'C3-M1'), ('col-3', 'C3-M2'), ('col-3', 'C3-M3'), ('col-3', 'C3-M4'),
                                                                  ('col-3', 'C3-M5'), ('col-3', 'C3-M6'), ('col-3', 'C3-M7'), ('col-3', 'C3-M8');


INSERT INTO member_referees (member_id, referee_id, relation) VALUES
                                                                  ('C1-M3', 'C1-M1', 'Non précisé'), ('C1-M3', 'C1-M2', 'Non précisé'),
                                                                  ('C1-M4', 'C1-M1', 'Non précisé'), ('C1-M4', 'C1-M2', 'Non précisé'),
                                                                  ('C1-M5', 'C1-M1', 'Non précisé'), ('C1-M5', 'C1-M2', 'Non précisé'),
                                                                  ('C1-M6', 'C1-M1', 'Non précisé'), ('C1-M6', 'C1-M2', 'Non précisé'),
                                                                  ('C1-M7', 'C1-M1', 'Non précisé'), ('C1-M7', 'C1-M2', 'Non précisé'),
                                                                  ('C1-M8', 'C1-M6', 'Non précisé'), ('C1-M8', 'C1-M7', 'Non précisé'),
                                                                  ('C3-M3', 'C3-M1', 'Non précisé'), ('C3-M3', 'C3-M2', 'Non précisé'),
                                                                  ('C3-M4', 'C3-M1', 'Non précisé'), ('C3-M4', 'C3-M2', 'Non précisé'),
                                                                  ('C3-M5', 'C3-M1', 'Non précisé'), ('C3-M5', 'C3-M2', 'Non précisé'),
                                                                  ('C3-M6', 'C3-M1', 'Non précisé'), ('C3-M6', 'C3-M2', 'Non précisé'),
                                                                  ('C3-M7', 'C3-M1', 'Non précisé'), ('C3-M7', 'C3-M2', 'Non précisé'),
                                                                  ('C3-M8', 'C3-M1', 'Non précisé'), ('C3-M8', 'C3-M2', 'Non précisé');


INSERT INTO membership_fees (id, collectivity_id, eligible_from, frequency, amount, label, status) VALUES
                                                                                                       ('cot-1', 'col-1', '2026-01-01', 'ANNUALLY', 100000.00, 'Cotisation annuelle', 'ACTIVE'),
                                                                                                       ('cot-2', 'col-2', '2026-01-01', 'ANNUALLY', 100000.00, 'Cotisation annuelle', 'ACTIVE'),
                                                                                                       ('cot-3', 'col-3', '2026-01-01', 'ANNUALLY',  50000.00, 'Cotisation annuelle', 'ACTIVE');


INSERT INTO financial_accounts (id, collectivity_id, account_type, amount, holder_name, mobile_money, mobile_number) VALUES
                                                                                                                         ('C1-A-CASH',     'col-1', 'CASH',           0, NULL,             NULL,           NULL),
                                                                                                                         ('C1-A-MOBILE-1', 'col-1', 'MOBILE_BANKING', 0, 'Mpanorina',      'ORANGE_MONEY', 370489612),
                                                                                                                         ('C2-A-CASH',     'col-2', 'CASH',           0, NULL,             NULL,           NULL),
                                                                                                                         ('C2-A-MOBILE-1', 'col-2', 'MOBILE_BANKING', 0, 'Dobo voalohany', 'ORANGE_MONEY', 320489612),
                                                                                                                         ('C3-A-CASH',     'col-3', 'CASH',           0, NULL,             NULL,           NULL);


INSERT INTO collectivities_transactions (id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode) VALUES
                                                                                                                                       ('TXN-C1-M1', '2026-01-01', 100000.00, 'col-1', 'C1-M1', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M2', '2026-01-01', 100000.00, 'col-1', 'C1-M2', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M3', '2026-01-01', 100000.00, 'col-1', 'C1-M3', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M4', '2026-01-01', 100000.00, 'col-1', 'C1-M4', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M5', '2026-01-01', 100000.00, 'col-1', 'C1-M5', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M6', '2026-01-01', 100000.00, 'col-1', 'C1-M6', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M7', '2026-01-01',  60000.00, 'col-1', 'C1-M7', 'C1-A-CASH', 'CASH'),
                                                                                                                                       ('TXN-C1-M8', '2026-01-01',  90000.00, 'col-1', 'C1-M8', 'C1-A-CASH', 'CASH');



INSERT INTO collectivities_transactions (id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode) VALUES
                                                                                                                                       ('TXN-C2-M1', '2026-01-01',  60000.00, 'col-2', 'C1-M1', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M2', '2026-01-01',  90000.00, 'col-2', 'C1-M2', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M3', '2026-01-01', 100000.00, 'col-2', 'C1-M3', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M4', '2026-01-01', 100000.00, 'col-2', 'C1-M4', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M5', '2026-01-01', 100000.00, 'col-2', 'C1-M5', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M6', '2026-01-01', 100000.00, 'col-2', 'C1-M6', 'C2-A-CASH',     'CASH'),
                                                                                                                                       ('TXN-C2-M7', '2026-01-01',  40000.00, 'col-2', 'C1-M7', 'C2-A-MOBILE-1', 'MOBILE_MONEY'),
