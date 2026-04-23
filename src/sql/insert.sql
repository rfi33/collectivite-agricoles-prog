INSERT INTO collectivities (id, number, name, location, specialization, federation_approval) VALUES
                                                                                                 ('col-1', 1, 'Mpanorina',      'Ambatondrazaka', 'RIZICULTURE',  TRUE),
                                                                                                 ('col-2', 2, 'Dobo voalohany', 'Ambatondrazaka', 'PISCICULTURE', TRUE),
                                                                                                 ('col-3', 3, 'Tantely mamy',   'Brickaville',    'APICULTURE',   TRUE);


INSERT INTO members (
    id, first_name, last_name, birth_date, gender,
    address, profession, phone_number, email,
    occupation, collectivity_id, join_date,
    registration_fee_paid, membership_dues_paid
) VALUES
      ('C1-M1', 'Prénom membre 1',  'Nom membre 1',  '1980-02-01', 'MALE',
       'Lot II V M Ambatondrazaka',  'Riziculteur', '0341234567', 'member.1@fed-agri.mg',
       'PRESIDENT',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M2', 'Prénom membre 2',  'Nom membre 2',  '1982-03-05', 'MALE',
       'Lot II F Ambatondrazaka',    'Agriculteur', '0321234567', 'member.2@fed-agri.mg',
       'VICE_PRESIDENT', 'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M3', 'Prénom membre 3',  'Nom membre 3',  '1992-03-10', 'MALE',
       'Lot II J Ambatondrazaka',    'Collecteur',  '0331234567', 'member.3@fed-agri.mg',
       'SECRETARY',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M4', 'Prénom membre 4',  'Nom membre 4',  '1988-05-22', 'FEMALE',
       'Lot A K 50 Ambatondrazaka',  'Distributeur','0381234567', 'member.4@fed-agri.mg',
       'TREASURER',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M5', 'Prénom membre 5',  'Nom membre 5',  '1999-08-21', 'MALE',
       'Lot UV 80 Ambatondrazaka',   'Riziculteur', '0373434567', 'member.5@fed-agri.mg',
       'CONFIRMED',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M6', 'Prénom membre 6',  'Nom membre 6',  '1998-08-22', 'FEMALE',
       'Lot UV 6 Ambatondrazaka',    'Riziculteur', '0372234567', 'member.6@fed-agri.mg',
       'CONFIRMED',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M7', 'Prénom membre 7',  'Nom membre 7',  '1998-01-31', 'MALE',
       'Lot UV 7 Ambatondrazaka',    'Riziculteur', '0374234567', 'member.7@fed-agri.mg',
       'CONFIRMED',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C1-M8', 'Prénom membre 8',  'Nom membre 8',  '1975-08-20', 'MALE',
       'Lot UV 8 Ambatondrazaka',    'Riziculteur', '0370234567', 'member.8@fed-agri.mg',
       'CONFIRMED',      'col-1', '2024-01-01', TRUE, TRUE),
      ('C3-M1', 'Prénom membre 9',  'Nom membre 9',  '1988-01-02', 'MALE',
       'Lot 33 J Antsirabe',         'Apiculteur',  '034034567',  'member.9@fed-agri.mg',
       'PRESIDENT',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M2', 'Prénom membre 10', 'Nom membre 10', '1982-03-05', 'MALE',
       'Lot 2 J Antsirabe',          'Agriculteur', '0338634567', 'member.10@fed-agri.mg',
       'VICE_PRESIDENT', 'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M3', 'Prénom membre 11', 'Nom membre 11', '1992-03-12', 'MALE',
       'Lot 8 KM Antsirabe',         'Collecteur',  '0338234567', 'member.11@fed-agri.mg',
       'SECRETARY',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M4', 'Prénom membre 12', 'Nom membre 12', '1988-05-10', 'FEMALE',
       'Lot A K 50 Antsirabe',       'Distributeur','0382334567', 'member.12@fed-agri.mg',
       'TREASURER',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M5', 'Prénom membre 13', 'Nom membre 13', '1999-08-11', 'MALE',
       'Lot UV 80 Antsirabe',        'Apiculteur',  '0373365567', 'member.13@fed-agri.mg',
       'CONFIRMED',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M6', 'Prénom membre 14', 'Nom membre 14', '1998-08-09', 'FEMALE',
       'Lot UV 6 Antsirabe',         'Apiculteur',  '0378234567', 'member.14@fed-agri.mg',
       'CONFIRMED',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M7', 'Prénom membre 15', 'Nom membre 15', '1998-01-13', 'MALE',
       'Lot UV 7 Antsirabe',         'Apiculteur',  '0374914567', 'member.15@fed-agri.mg',
       'CONFIRMED',      'col-3', '2024-01-01', TRUE, TRUE),
      ('C3-M8', 'Prénom membre 16', 'Nom membre 16', '1975-08-02', 'MALE',
       'Lot UV 8 Antsirabe',         'Apiculteur',  '0370634567', 'member.16@fed-agri.mg',
       'CONFIRMED',      'col-3', '2024-01-01', TRUE, TRUE);


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
                                                                  ('col-1', 'C1-M5'), ('col-1', 'C1-M6'), ('col-1', 'C1-M7'), ('col-1', 'C1-M8');

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
                                                                  ('col-2', 'C1-M1'), ('col-2', 'C1-M2'), ('col-2', 'C1-M3'), ('col-2', 'C1-M4'),
                                                                  ('col-2', 'C1-M5'), ('col-2', 'C1-M6'), ('col-2', 'C1-M7'), ('col-2', 'C1-M8');

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
                                                                  ('col-3', 'C3-M1'), ('col-3', 'C3-M2'), ('col-3', 'C3-M3'), ('col-3', 'C3-M4'),
                                                                  ('col-3', 'C3-M5'), ('col-3', 'C3-M6'), ('col-3', 'C3-M7'), ('col-3', 'C3-M8');


INSERT INTO member_referees (member_id, referee_id, relation) VALUES
                                                                  ('C1-M3', 'C1-M1', 'Parrain interne'),
                                                                  ('C1-M3', 'C1-M2', 'Parrain interne'),
                                                                  ('C1-M4', 'C1-M1', 'Parrain interne'),
                                                                  ('C1-M4', 'C1-M2', 'Parrain interne'),
                                                                  ('C1-M5', 'C1-M1', 'Parrain interne'),
                                                                  ('C1-M5', 'C1-M2', 'Parrain interne'),
                                                                  ('C1-M6', 'C1-M1', 'Parrain interne'),
                                                                  ('C1-M6', 'C1-M2', 'Parrain interne'),
                                                                  ('C1-M7', 'C1-M1', 'Parrain interne'),
                                                                  ('C1-M7', 'C1-M2', 'Parrain interne'),
                                                                  ('C1-M8', 'C1-M6', 'Parrain interne'),
                                                                  ('C1-M8', 'C1-M7', 'Parrain interne');

INSERT INTO member_referees (member_id, referee_id, relation) VALUES
                                                                  ('C3-M1', 'C1-M1', 'Parrain externe'),
                                                                  ('C3-M1', 'C1-M2', 'Parrain externe'),
                                                                  ('C3-M2', 'C1-M1', 'Parrain externe'),
                                                                  ('C3-M2', 'C1-M2', 'Parrain externe'),
                                                                  ('C3-M3', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M3', 'C3-M2', 'Parrain interne'),
                                                                  ('C3-M4', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M4', 'C3-M2', 'Parrain interne'),
                                                                  ('C3-M5', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M5', 'C3-M2', 'Parrain interne'),
                                                                  ('C3-M6', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M6', 'C3-M2', 'Parrain interne'),
                                                                  ('C3-M7', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M7', 'C3-M2', 'Parrain interne'),
                                                                  ('C3-M8', 'C3-M1', 'Parrain interne'),
                                                                  ('C3-M8', 'C3-M2', 'Parrain interne');


INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount, holder_name, mobile_money, mobile_number
) VALUES
      ('C1-A-CASH',     'col-1', 'CASH',           0, NULL,        NULL,           NULL),
      ('C1-A-MOBILE-1', 'col-1', 'MOBILE_BANKING',  0, 'Mpanorina', 'ORANGE_MONEY', 370489612);

INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount, holder_name, mobile_money, mobile_number
) VALUES
      ('C2-A-CASH',     'col-2', 'CASH',           0, NULL,             NULL,           NULL),
      ('C2-A-MOBILE-1', 'col-2', 'MOBILE_BANKING',  0, 'Dobo voalohany', 'ORANGE_MONEY', 320489612);

INSERT INTO financial_accounts (
    id, collectivity_id, account_type, amount, holder_name
) VALUES
    ('C3-A-CASH', 'col-3', 'CASH', 0, NULL);


INSERT INTO collectivities_transactions (
    id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode
) VALUES
      ('TX-C1-M1', '2026-01-01', 100000, 'col-1', 'C1-M1', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M2', '2026-01-01', 100000, 'col-1', 'C1-M2', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M3', '2026-01-01', 100000, 'col-1', 'C1-M3', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M4', '2026-01-01', 100000, 'col-1', 'C1-M4', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M5', '2026-01-01', 100000, 'col-1', 'C1-M5', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M6', '2026-01-01', 100000, 'col-1', 'C1-M6', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M7', '2026-01-01',  60000, 'col-1', 'C1-M7', 'C1-A-CASH', 'CASH'),
      ('TX-C1-M8', '2026-01-01',  90000, 'col-1', 'C1-M8', 'C1-A-CASH', 'CASH');

UPDATE financial_accounts SET amount = 750000 WHERE id = 'C1-A-CASH';


INSERT INTO collectivities_transactions (
    id, creation_date, amount, collectivity_id, member_id, account_credited_id, payment_mode
) VALUES
      ('TX-C2-M1', '2026-01-01',  60000, 'col-2', 'C1-M1', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M2', '2026-01-01',  90000, 'col-2', 'C1-M2', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M3', '2026-01-01', 100000, 'col-2', 'C1-M3', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M4', '2026-01-01', 100000, 'col-2', 'C1-M4', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M5', '2026-01-01', 100000, 'col-2', 'C1-M5', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M6', '2026-01-01', 100000, 'col-2', 'C1-M6', 'C2-A-CASH',     'CASH'),
      ('TX-C2-M7', '2026-01-01',  40000, 'col-2', 'C1-M7', 'C2-A-MOBILE-1', 'MOBILE_BANKING'),
      ('TX-C2-M8', '2026-01-01',  60000, 'col-2', 'C1-M8', 'C2-A-MOBILE-1', 'MOBILE_BANKING');

UPDATE financial_accounts SET amount = 550000 WHERE id = 'C2-A-CASH';
UPDATE financial_accounts SET amount = 100000 WHERE id = 'C2-A-MOBILE-1';
