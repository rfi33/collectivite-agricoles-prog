INSERT INTO collectivities (id, location, federation_approval) VALUES
                                                                   ('col-001', 'Antananarivo, Analamanga',      TRUE),
                                                                   ('col-002', 'Toamasina, Atsinanana',         TRUE),
                                                                   ('col-003', 'Mahajanga, Boeny',              FALSE),
                                                                   ('col-004', 'Fianarantsoa, Haute Matsiatra', FALSE);

INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, join_date, registration_fee_paid, membership_dues_paid) VALUES
                                                                                                                                                                                                       ('mem-001', 'Rakoto',     'Andrianaivo',       '1975-03-12', 'MALE',   '12 Rue Independance, Antananarivo',    'Ingenieur',     '+261341000001', 'rakoto.andrianaivo@email.mg',  'PRESIDENT',      'col-001', '2021-05-10', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-002', 'Voahangy',   'Rasoamahenina',     '1980-07-24', 'FEMALE', '45 Avenue Liberation, Antananarivo',  'Medecin',       '+261341000002', 'voahangy.r@email.mg',          'VICE_PRESIDENT', 'col-001', '2021-08-15', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-003', 'Hery',       'Rakotondrazaka',    '1983-11-05', 'MALE',   '8 Cite Ampefiloha, Antananarivo',     'Comptable',     '+261341000003', 'hery.rakoto@email.mg',         'TREASURER',      'col-001', '2021-11-20', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-004', 'Mialy',      'Raharinoro',        '1990-05-18', 'FEMALE', '23 Rue Rainandriamampandry, Tana',    'Enseignante',   '+261341000004', 'mialy.raha@email.mg',          'SECRETARY',      'col-001', '2022-01-10', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-005', 'Naina',      'Razafindrakoto',    '1995-09-30', 'MALE',   '67 Lotissement Ankadifotsy, Tana',    'Etudiant',      '+261341000005', 'naina.raza@email.mg',          'JUNIOR',         'col-001', '2022-04-05', TRUE,  FALSE),
                                                                                                                                                                                                       ('mem-006', 'Fanja',      'Rakotomalala',      '1988-12-01', 'FEMALE', '3 Cite Isotry, Antananarivo',         'Agricultrice',  '+261341000006', 'fanja.rakoto@email.mg',        'SENIOR',         'col-001', '2021-03-18', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-007', 'Tiana',      'Andrianasolo',      '1992-06-22', 'MALE',   '15 Rue Pasteur, Antananarivo',        'Technicien',    '+261341000007', 'tiana.andria@email.mg',        'JUNIOR',         'col-001', '2022-07-30', TRUE,  FALSE),
                                                                                                                                                                                                       ('mem-008', 'Lalao',      'Rakotondrabe',      '1979-02-14', 'FEMALE', '9 Quartier Tsaralalana, Tana',        'Infirmiere',    '+261341000008', 'lalao.rako@email.mg',          'SENIOR',         'col-001', '2021-06-01', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-009', 'Andry',      'Rabemananjara',     '1985-10-09', 'MALE',   '30 Rue du Commerce, Antananarivo',    'Commercant',    '+261341000009', 'andry.rabe@email.mg',          'SENIOR',         'col-001', '2022-02-20', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-010', 'Sahondra',   'Randriamihaja',     '1993-04-17', 'FEMALE', '5 Cite Mahamasina, Antananarivo',     'Secretaire',    '+261341000010', 'sahondra.rand@email.mg',       'JUNIOR',         'col-001', '2023-01-15', FALSE, FALSE),
                                                                                                                                                                                                       ('mem-011', 'Olivier',    'Ramaroson',         '1978-08-03', 'MALE',   '1 Bd Ratsimilaho, Toamasina',         'Pecheur',       '+261341000011', 'olivier.ramas@email.mg',       'PRESIDENT',      'col-002', '2020-11-05', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-012', 'Haingo',     'Razanajatovo',      '1982-03-29', 'FEMALE', '14 Rue Jean-Ralaimongo, Toamasina',   'Institutrice',  '+261341000012', 'haingo.raza@email.mg',         'VICE_PRESIDENT', 'col-002', '2021-01-20', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-013', 'Freddy',     'Rakotondratsima',   '1986-09-14', 'MALE',   '22 Cite Tanambao, Toamasina',         'Mecanicien',    '+261341000013', 'freddy.rako@email.mg',         'TREASURER',      'col-002', '2021-04-10', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-014', 'Rindra',     'Andriamahefa',      '1991-12-08', 'FEMALE', '7 Rue Colbert, Toamasina',            'Pharmacienne',  '+261341000014', 'rindra.andria@email.mg',       'SECRETARY',      'col-002', '2021-07-22', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-015', 'Joel',       'Rasoloarivony',     '1984-05-25', 'MALE',   '18 Quartier Ambalakisoa, Toamasina',  'Chauffeur',     '+261341000015', 'joel.raso@email.mg',           'SENIOR',         'col-002', '2021-02-14', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-016', 'Clemence',   'Rakotoarisoa',      '1997-11-11', 'FEMALE', '6 Rue du Port, Toamasina',            'Vendeuse',      '+261341000016', 'clemence.rako@email.mg',       'JUNIOR',         'col-002', '2022-05-03', TRUE,  FALSE),
                                                                                                                                                                                                       ('mem-017', 'Patrick',    'Andrianarivo',      '1989-01-30', 'MALE',   '11 Avenue Independance, Toamasina',   'Electricien',   '+261341000017', 'patrick.andria@email.mg',      'SENIOR',         'col-002', '2021-09-17', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-018', 'Volatiana',  'Ranarivelo',        '1994-07-06', 'FEMALE', '33 Cite Morarano, Toamasina',         'Couturiere',    '+261341000018', 'volatiana.rana@email.mg',      'JUNIOR',         'col-002', '2023-03-08', FALSE, FALSE),
                                                                                                                                                                                                       ('mem-019', 'Berthine',   'Ravololomanga',     '1981-06-19', 'FEMALE', '4 Rue Ratsimilaho, Mahajanga',        'Agricultrice',  '+261341000019', 'berthine.rav@email.mg',        'PRESIDENT',      'col-003', '2023-10-01', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-020', 'Jean-Paul',  'Rasoarilanto',      '1979-09-23', 'MALE',   '27 Quartier Mahabibo, Mahajanga',     'Pecheur',       '+261341000020', 'jeanpaul.raso@email.mg',       'VICE_PRESIDENT', 'col-003', '2024-01-15', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-021', 'Honorine',   'Rakotondrabe',      '1987-04-02', 'FEMALE', '9 Cite Amborovy, Mahajanga',          'Infirmiere',    '+261341000021', 'honorine.rako@email.mg',       'TREASURER',      'col-003', '2024-02-10', FALSE, FALSE),
                                                                                                                                                                                                       ('mem-022', 'Cedric',     'Randrianantoandro', '1993-08-16', 'MALE',   '16 Rue Gallieni, Mahajanga',          'Menuisier',     '+261341000022', 'cedric.rand@email.mg',         'SECRETARY',      'col-003', '2024-03-05', FALSE, FALSE),
                                                                                                                                                                                                       ('mem-023', 'Arsene',     'Rabearimanana',     '1976-01-28', 'MALE',   '2 Rue Haute-Ville, Fianarantsoa',     'Enseignant',    '+261341000023', 'arsene.rabe@email.mg',         'PRESIDENT',      'col-004', '2022-05-12', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-024', 'Dieudonne',  'Andriantsoa',       '1983-11-11', 'MALE',   '18 Cite Tanambao, Fianarantsoa',      'Comptable',     '+261341000024', 'dieudonne.andria@email.mg',    'VICE_PRESIDENT', 'col-004', '2022-08-20', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-025', 'Volasoa',    'Rakotondratsara',   '1990-03-07', 'FEMALE', '5 Avenue Republique, Fianarantsoa',   'Medecin',       '+261341000025', 'volasoa.rako@email.mg',        'TREASURER',      'col-004', '2022-11-30', TRUE,  TRUE),
                                                                                                                                                                                                       ('mem-026', 'Nirina',     'Razakamahefa',      '1995-06-14', 'FEMALE', '10 Rue du Marche, Fianarantsoa',      'Etudiante',     '+261341000026', 'nirina.raza@email.mg',         'SECRETARY',      'col-004', '2023-02-01', TRUE,  FALSE);

UPDATE collectivities SET president_id = 'mem-001', vice_president_id = 'mem-002', treasurer_id = 'mem-003', secretary_id = 'mem-004' WHERE id = 'col-001';
UPDATE collectivities SET president_id = 'mem-011', vice_president_id = 'mem-012', treasurer_id = 'mem-013', secretary_id = 'mem-014' WHERE id = 'col-002';
UPDATE collectivities SET president_id = 'mem-019', vice_president_id = 'mem-020', treasurer_id = 'mem-021', secretary_id = 'mem-022' WHERE id = 'col-003';
UPDATE collectivities SET president_id = 'mem-023', vice_president_id = 'mem-024', treasurer_id = 'mem-025', secretary_id = 'mem-026' WHERE id = 'col-004';

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
                                                                  ('col-001', 'mem-001'), ('col-001', 'mem-002'), ('col-001', 'mem-003'), ('col-001', 'mem-004'), ('col-001', 'mem-005'),
                                                                  ('col-001', 'mem-006'), ('col-001', 'mem-007'), ('col-001', 'mem-008'), ('col-001', 'mem-009'), ('col-001', 'mem-010'),
                                                                  ('col-002', 'mem-011'), ('col-002', 'mem-012'), ('col-002', 'mem-013'), ('col-002', 'mem-014'), ('col-002', 'mem-015'),
                                                                  ('col-002', 'mem-016'), ('col-002', 'mem-017'), ('col-002', 'mem-018'),
                                                                  ('col-003', 'mem-019'), ('col-003', 'mem-020'), ('col-003', 'mem-021'), ('col-003', 'mem-022'),
                                                                  ('col-004', 'mem-023'), ('col-004', 'mem-024'), ('col-004', 'mem-025'), ('col-004', 'mem-026');

INSERT INTO member_referees (member_id, referee_id, relation) VALUES
                                                                  ('mem-005', 'mem-001', 'Collegue agricole'),
                                                                  ('mem-005', 'mem-006', 'Voisin de quartier'),
                                                                  ('mem-007', 'mem-003', 'Ami de longue date'),
                                                                  ('mem-007', 'mem-008', 'Parent eloigne'),
                                                                  ('mem-010', 'mem-004', 'Collegue de travail'),
                                                                  ('mem-010', 'mem-009', 'Voisin'),
                                                                  ('mem-016', 'mem-011', 'Cousin'),
                                                                  ('mem-016', 'mem-015', 'Collegue pecheur'),
                                                                  ('mem-018', 'mem-012', 'Amie d''enfance'),
                                                                  ('mem-018', 'mem-017', 'Voisin'),
                                                                  ('mem-021', 'mem-019', 'Soeur'),
                                                                  ('mem-022', 'mem-020', 'Ami de quartier'),
                                                                  ('mem-026', 'mem-023', 'Ancienne eleve');
INSERT INTO membership_fees (
    id,
    collectivity_id,
    eligible_from,
    frequency,
    amount,
    label,
    status
) VALUES
      ('fee-001', 'col-001', '2021-06-01', 'MONTHLY',     5000.00,  'Monthly membership dues',      'ACTIVE'),
      ('fee-002', 'col-001', '2021-06-01', 'ANNUALLY',   50000.00,  'Annual membership dues',       'ACTIVE'),
      ('fee-003', 'col-001', '2022-01-01', 'PUNCTUALLY', 10000.00,  'Special event contribution',   'INACTIVE'),
      ('fee-004', 'col-002', '2021-03-01', 'MONTHLY',     4000.00,  'Monthly membership dues',      'ACTIVE'),
      ('fee-005', 'col-002', '2021-03-01', 'ANNUALLY',   40000.00,  'Annual membership dues',       'ACTIVE'),
      ('fee-006', 'col-002', '2023-06-01', 'PUNCTUALLY',  8000.00,  'Emergency fund contribution',  'ACTIVE');


INSERT INTO collectivities_transactions (
    id,
    creation_date,
    amount,
    collectivity_id,
    member_id,
    account_credited_id,
    payment_mode
) VALUES
      ('txn-001', '2024-01-05',  5000.00, 'col-001', 'mem-001', 'account-cash-001',  'CASH'),
      ('txn-002', '2024-01-06',  5000.00, 'col-001', 'mem-002', 'account-cash-001',  'CASH'),
      ('txn-003', '2024-01-08',  5000.00, 'col-001', 'mem-003', 'account-mvola-001', 'MOBILE_BANKING'),
      ('txn-004', '2024-01-10',  5000.00, 'col-001', 'mem-004', 'account-mvola-001', 'MOBILE_BANKING'),
      ('txn-005', '2024-02-03',  5000.00, 'col-001', 'mem-001', 'account-cash-001',  'CASH'),
      ('txn-006', '2024-02-05',  5000.00, 'col-001', 'mem-006', 'account-cash-001',  'CASH'),
      ('txn-007', '2024-02-07',  5000.00, 'col-001', 'mem-008', 'account-bank-001',  'BANK_TRANSFER'),
      ('txn-008', '2024-02-09',  5000.00, 'col-001', 'mem-009', 'account-bank-001',  'BANK_TRANSFER'),
      ('txn-009', '2024-03-04',  5000.00, 'col-001', 'mem-002', 'account-cash-001',  'CASH'),
      ('txn-010', '2024-03-06', 10000.00, 'col-001', 'mem-003', 'account-cash-001',  'CASH'),
      ('txn-011', '2024-01-07',  4000.00, 'col-002', 'mem-011', 'account-cash-002',  'CASH'),
      ('txn-012', '2024-01-09',  4000.00, 'col-002', 'mem-012', 'account-cash-002',  'CASH'),
      ('txn-013', '2024-01-11',  4000.00, 'col-002', 'mem-013', 'account-mvola-002', 'MOBILE_BANKING'),
      ('txn-014', '2024-01-15',  4000.00, 'col-002', 'mem-015', 'account-mvola-002', 'MOBILE_BANKING'),
      ('txn-015', '2024-02-04',  4000.00, 'col-002', 'mem-011', 'account-cash-002',  'CASH'),
      ('txn-016', '2024-02-06',  4000.00, 'col-002', 'mem-017', 'account-bank-002',  'BANK_TRANSFER'),
      ('txn-017', '2024-03-05',  8000.00, 'col-002', 'mem-012', 'account-cash-002',  'CASH');


INSERT INTO financial_accounts (id, collectivity_id, account_type, amount)
VALUES ('acc-001', 'col-001', 'CASH', 1500000.00);

INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, bank_name, bank_code, bank_branch_code,
    bank_account_number, bank_account_key
) VALUES (
             'acc-002', 'col-001', 'BANK', 8750000.00,
             'Collectivité Agri Antananarivo',
             'BOA', 10005, 00001, 12345678901, 27
         );
INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, mobile_money, mobile_number
) VALUES (
             'acc-003', 'col-001', 'MOBILE_BANKING', 320000.00,
             'Hery Rakotondrazaka',
             'MVOLA', 341000003
         );

INSERT INTO financial_accounts (id, collectivity_id, account_type, amount)
VALUES ('acc-004', 'col-002', 'CASH', 750000.00);

INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, bank_name, bank_code, bank_branch_code,
    bank_account_number, bank_account_key
) VALUES (
             'acc-005', 'col-002', 'BANK', 4200000.00,
             'Collectivité Agri Toamasina',
             'MCB', 10002, 00002, 98765432101, 14
         );
INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, mobile_money, mobile_number
) VALUES (
             'acc-006', 'col-002', 'MOBILE_BANKING', 180000.00,
             'Freddy Rakotondratsima',
             'AIRTEL_MONEY', 341000013
         );
INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, bank_name, bank_code, bank_branch_code,
    bank_account_number, bank_account_key
) VALUES (
             'acc-007', 'col-002', 'BANK', 1100000.00,
             'Freddy Rakotondratsima',
             'BMOI', 10003, 00005, 11122233301, 09
         );
INSERT INTO financial_accounts (id, collectivity_id, account_type, amount)
VALUES ('acc-008', 'col-003', 'CASH', 200000.00);
INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, mobile_banking_money, mobile_number
) VALUES (
             'acc-009', 'col-003', 'MOBILE_BANKING', 95000.00,
             'Honorine Rakotondrabe',
             'ORANGE_MONEY', 341000021
         );

INSERT INTO financial_accounts (id, collectivity_id, account_type, amount)
VALUES ('acc-010', 'col-004', 'CASH', 500000.00);

INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, bank_name, bank_code, bank_branch_code,
    bank_account_number, bank_account_key
) VALUES (
             'acc-011', 'col-004', 'BANK', 3300000.00,
             'Collectivité Agri Fianarantsoa',
             'BRED', 10001, 00003, 55566677801, 31
         );
INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount,
    holder_name, mobile_money, mobile_number
) VALUES (
             'acc-012', 'col-004', 'MOBILE_BANKING', 260000.00,
             'Volasoa Rakotondratsara',
             'MVOLA', 341000025
         );
INSERT INTO collectivities_transactions (
    id, creation_date, amount, collectivity_id,
    member_id, account_credited_id, payment_mode
) VALUES
      ('tx-001', '2026-01-15', 50000.00, 'col-001', 'mem-001', 'acc-002', 'BANK_TRANSFER'),
      ('tx-002', '2026-01-15', 50000.00, 'col-001', 'mem-002', 'acc-002', 'BANK_TRANSFER'),
      ('tx-003', '2026-01-15', 50000.00, 'col-001', 'mem-003', 'acc-003', 'MOBILE_BANKING'),
      ('tx-004', '2026-01-15', 50000.00, 'col-001', 'mem-004', 'acc-001', 'CASH'),
      ('tx-005', '2026-02-10', 50000.00, 'col-001', 'mem-006', 'acc-002', 'BANK_TRANSFER'),
      ('tx-006', '2026-02-10', 50000.00, 'col-001', 'mem-008', 'acc-003', 'MOBILE_BANKING'),
      ('tx-007', '2026-02-10', 50000.00, 'col-001', 'mem-009', 'acc-001', 'CASH'),
      ('tx-008', '2026-03-05', 200000.00, 'col-001', 'mem-001', 'acc-002', 'BANK_TRANSFER'),
      ('tx-009', '2026-01-20', 50000.00, 'col-002', 'mem-011', 'acc-005', 'BANK_TRANSFER'),
      ('tx-010', '2026-01-20', 50000.00, 'col-002', 'mem-012', 'acc-005', 'BANK_TRANSFER'),
      ('tx-011', '2026-01-20', 50000.00, 'col-002', 'mem-013', 'acc-006', 'MOBILE_BANKING'),
      ('tx-012', '2026-02-18', 50000.00, 'col-002', 'mem-015', 'acc-004', 'CASH'),
      ('tx-013', '2026-02-18', 50000.00, 'col-002', 'mem-017', 'acc-007', 'BANK_TRANSFER'),
      ('tx-014', '2026-01-25', 50000.00, 'col-003', 'mem-019', 'acc-008', 'CASH'),
      ('tx-015', '2026-02-25', 50000.00, 'col-003', 'mem-020', 'acc-009', 'MOBILE_BANKING'),
      ('tx-016', '2026-01-10', 50000.00, 'col-004', 'mem-023', 'acc-011', 'BANK_TRANSFER'),
      ('tx-017', '2026-01-10', 50000.00, 'col-004', 'mem-024', 'acc-011', 'BANK_TRANSFER'),
      ('tx-018', '2026-02-12', 50000.00, 'col-004', 'mem-025', 'acc-012', 'MOBILE_BANKING'),
      ('tx-019', '2026-03-01', 200000.00, 'col-004', 'mem-023', 'acc-010', 'CASH');