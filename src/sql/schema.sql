
DROP TABLE IF EXISTS "member_payment"        CASCADE;
DROP TABLE IF EXISTS "transaction"           CASCADE;
DROP TABLE IF EXISTS "mobile_banking_account" CASCADE;
DROP TABLE IF EXISTS "bank_account"          CASCADE;
DROP TABLE IF EXISTS "cash_account"          CASCADE;
DROP TABLE IF EXISTS "membership_fee"        CASCADE;
DROP TABLE IF EXISTS "member_referee"        CASCADE;
DROP TABLE IF EXISTS "collectivity_member"   CASCADE;
DROP TABLE IF EXISTS "collectivity"          CASCADE;
DROP TABLE IF EXISTS "member"                CASCADE;

DROP TYPE IF EXISTS payment_mode         CASCADE;
DROP TYPE IF EXISTS transaction_type     CASCADE;
DROP TYPE IF EXISTS mobile_banking_service CASCADE;
DROP TYPE IF EXISTS bank_name            CASCADE;
DROP TYPE IF EXISTS activity_status      CASCADE;
DROP TYPE IF EXISTS frequency            CASCADE;
DROP TYPE IF EXISTS member_occupation    CASCADE;
DROP TYPE IF EXISTS gender               CASCADE;

CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE'
);

CREATE TYPE member_occupation AS ENUM (
    'JUNIOR',
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

CREATE TYPE mobile_banking_service AS ENUM (
    'AIRTEL_MONEY',
    'MVOLA',
    'ORANGE_MONEY'
);

CREATE TYPE payment_mode AS ENUM (
    'CASH',
    'MOBILE_BANKING',
    'BANK_TRANSFER'
);

CREATE TYPE transaction_type AS ENUM (
    'IN',
    'OUT'
);

CREATE TABLE "member"
(
    id                    VARCHAR(50)       PRIMARY KEY,
    first_name            VARCHAR(100)      NOT NULL,
    last_name             VARCHAR(100)      NOT NULL,
    birth_date            DATE,
    gender                gender,
    address               VARCHAR(255),
    profession            VARCHAR(100),
    phone_number          VARCHAR(20),
    email                 VARCHAR(150),
    occupation            member_occupation,
    registration_fee_paid BOOLEAN           NOT NULL DEFAULT FALSE,
    membership_dues_paid  BOOLEAN           NOT NULL DEFAULT FALSE
);

-- ============================================================
-- TABLE collectivity
-- ============================================================

CREATE TABLE "collectivity"
(
    id                VARCHAR(50)  PRIMARY KEY,
    name              VARCHAR(255) UNIQUE,
    number            INTEGER      UNIQUE,
    location          VARCHAR(255),
    specialization    VARCHAR(100),
    president_id      VARCHAR(50)  REFERENCES "member" (id),
    vice_president_id VARCHAR(50)  REFERENCES "member" (id),
    treasurer_id      VARCHAR(50)  REFERENCES "member" (id),
    secretary_id      VARCHAR(50)  REFERENCES "member" (id)
);

-- ============================================================
-- TABLE collectivity_member
-- Table de liaison membre <-> collectivité (Many-to-Many)
-- Un membre peut appartenir à plusieurs collectivités
-- ============================================================

CREATE TABLE "collectivity_member"
(
    id              VARCHAR(50) PRIMARY KEY,
    member_id       VARCHAR(50) NOT NULL REFERENCES "member" (id),
    collectivity_id VARCHAR(50) NOT NULL REFERENCES "collectivity" (id)
);

-- ============================================================
-- TABLE member_referee
-- Parrainage : un membre est parrainé par d'autres membres
-- ============================================================

CREATE TABLE "member_referee"
(
    id                 VARCHAR(50) PRIMARY KEY,
    member_refereed_id VARCHAR(50) NOT NULL REFERENCES "member" (id),
    member_referee_id  VARCHAR(50) NOT NULL REFERENCES "member" (id),
    CONSTRAINT chk_no_self_referee
        CHECK (member_referee_id <> member_refereed_id)
);

-- ============================================================
-- TABLE membership_fee
-- Cotisations définies par une collectivité
-- ============================================================

CREATE TABLE "membership_fee"
(
    id              VARCHAR(50)     PRIMARY KEY,
    label           VARCHAR(255),
    amount          NUMERIC(12, 2),
    eligible_from   DATE,
    status          activity_status,
    frequency       frequency,
    collectivity_id VARCHAR(50)     REFERENCES "collectivity" (id)
);

-- ============================================================
-- COMPTES FINANCIERS
-- Trois types : cash, bancaire, mobile banking
-- Le solde est calculé dynamiquement via les transactions
-- (cf. FinancialAccount.getBalanceAt() en Java)
-- ============================================================

-- Une seule caisse par collectivité (UNIQUE sur collectivity_id)
CREATE TABLE "cash_account"
(
    id              VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50) UNIQUE REFERENCES "collectivity" (id)
);

-- Plusieurs comptes bancaires possibles par collectivité
CREATE TABLE "bank_account"
(
    id              VARCHAR(50) PRIMARY KEY,
    holder_name     VARCHAR(255),
    bank_name       bank_name,
    bank_code       INTEGER,
    branch_code     INTEGER,
    account_number  INTEGER,
    key             INTEGER,
    collectivity_id VARCHAR(50) REFERENCES "collectivity" (id)
);

-- Plusieurs comptes mobile banking possibles par collectivité
CREATE TABLE "mobile_banking_account"
(
    id              VARCHAR(50)            PRIMARY KEY,
    holder_name     VARCHAR(255),
    service         mobile_banking_service,
    mobile_number   VARCHAR(20),
    collectivity_id VARCHAR(50)            REFERENCES "collectivity" (id)
);

-- ============================================================
-- TABLE transaction
-- Stocke tous les mouvements financiers
-- financial_account_id est polymorphe (cash / bank / mobile)
-- donc pas de FK directe — géré côté Java
-- member_debited_id : requis par TransactionRepository
-- ============================================================

CREATE TABLE "transaction"
(
    id                   VARCHAR(50)      PRIMARY KEY,
    amount               NUMERIC(12, 2)   NOT NULL,
    creation_date        DATE             NOT NULL DEFAULT CURRENT_DATE,
    transaction_type     transaction_type NOT NULL,
    financial_account_id VARCHAR(50)      NOT NULL,
    member_debited_id    VARCHAR(50)      REFERENCES "member" (id)
);

-- ============================================================
-- TABLE member_payment
-- Paiement d'un membre pour une cotisation donnée
-- financial_account_id est polymorphe (cash / bank / mobile)
-- donc pas de FK directe — géré côté Java
-- ============================================================

CREATE TABLE "member_payment"
(
    id                   VARCHAR(50)   PRIMARY KEY,
    amount               NUMERIC(12, 2),
    creation_date        DATE          NOT NULL DEFAULT CURRENT_DATE,
    member_debited_id    VARCHAR(50)   REFERENCES "member" (id),
    membership_fee_id    VARCHAR(50)   REFERENCES "membership_fee" (id),
    payment_mode         payment_mode,
    financial_account_id VARCHAR(50)
);