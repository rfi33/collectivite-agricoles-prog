CREATE TABLE collectivities (
                                id SERIAL PRIMARY KEY,
                                name TEXT NOT NULL,
                                number TEXT NOT NULL UNIQUE,
                                city TEXT NOT NULL,
                                specialty TEXT NOT NULL,
                                creation_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         first_name TEXT NOT NULL,
                         last_name TEXT NOT NULL,
                         collectivity_id INT REFERENCES collectivities(id),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);