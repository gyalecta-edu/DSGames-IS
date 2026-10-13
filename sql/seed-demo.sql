USE dsgames;
-- SOLO SVILUPPO: credenziali di esempio conosciute, NON usare in produzione.
-- Admin di default (password: admin123)
INSERT INTO utente (nome, cognome, email, password_hash, ruolo)
VALUES ('Admin', 'DSGames', 'admin@dsgames.it',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        'ADMIN');

INSERT INTO prodotto (nome, descrizione, categoria, piattaforma, prezzo, quantita, in_offerta, prezzo_offerta) VALUES
('PlayStation 5',
 'La console di nuova generazione Sony con SSD ultra-veloce e controller DualSense con feedback aptico.',
 'CONSOLE', 'PlayStation 5', 549.99, 10, FALSE, 0),

('Xbox Series X',
 'La console Microsoft più potente di sempre. 12 teraflop di GPU, SSD da 1TB e retrocompatibilità.',
 'CONSOLE', 'Xbox Series X', 499.99, 8, FALSE, 0),

('Nintendo Switch OLED',
 'Il modello OLED con schermo vivido da 7 pollici, dock con porta LAN e 64GB di storage interno.',
 'CONSOLE', 'Nintendo Switch', 349.99, 15, TRUE, 299.99),

('The Legend of Zelda: Tears of the Kingdom',
 'Link torna ad esplorare Hyrule e i cieli che la sovrastano in questo sequel di Breath of the Wild.',
 'VIDEOGIOCO', 'Nintendo Switch', 59.99, 20, FALSE, 0),

('God of War: Ragnarok',
 'Kratos e Atreus si preparano ad affrontare il Ragnarok. Epico action-adventure nordico.',
 'VIDEOGIOCO', 'PlayStation 5', 49.99, 12, TRUE, 34.99),

('Elden Ring',
 'Un gioco di ruolo open world sviluppato da FromSoftware in collaborazione con George R.R. Martin.',
 'VIDEOGIOCO', 'PlayStation 5', 59.99, 18, FALSE, 0),

('Forza Horizon 5',
 'Il racing game open world più bello mai realizzato, ambientato in Messico con oltre 500 auto.',
 'VIDEOGIOCO', 'Xbox Series X', 39.99, 25, TRUE, 24.99),

('Steam Deck',
 'Il PC gaming portatile di Valve. Gioca ai tuoi giochi Steam ovunque tu voglia.',
 'CONSOLE', 'PC', 419.99, 5, FALSE, 0);

-- Utente di test (password: test1234)
INSERT INTO utente (nome, cognome, email, password_hash, ruolo,
                    indirizzo, citta, cap)
VALUES ('Mario', 'Rossi', 'mario@test.it',
        '937e8d5fbb48bd4949536cd65b8d35c426b80d2f830c5c308e2cdec422ae2244',
        'USER', 'Via Roma 42', 'Salerno', '84100');