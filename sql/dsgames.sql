CREATE DATABASE IF NOT EXISTS dsgames
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dsgames;

-- Tabella utenti (clienti + admin)
CREATE TABLE utente (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(50)  NOT NULL,
    cognome       VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    ruolo         ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    indirizzo     VARCHAR(200),
    citta         VARCHAR(100),
    cap           VARCHAR(10),
    telefono      VARCHAR(20),
    data_reg      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabella prodotti (console e videogiochi)
CREATE TABLE prodotto (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    descrizione     TEXT,
    categoria       ENUM('CONSOLE', 'VIDEOGIOCO') NOT NULL,
    piattaforma     VARCHAR(50),
    prezzo          DECIMAL(10,2) NOT NULL CHECK (prezzo > 0),
    quantita        INT NOT NULL DEFAULT 0 CHECK (quantita >= 0),
    immagine        VARCHAR(255),
    in_offerta      BOOLEAN NOT NULL DEFAULT FALSE,
    prezzo_offerta  DECIMAL(10,2),
    eliminato       BOOLEAN NOT NULL DEFAULT FALSE
);

-- Tabella ordini
CREATE TABLE ordine (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    id_utente           INT NOT NULL,
    data_ordine         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stato               ENUM('IN_ELABORAZIONE','SPEDITO','CONSEGNATO') NOT NULL DEFAULT 'IN_ELABORAZIONE',
    indirizzo_sped      VARCHAR(200) NOT NULL,
    citta_sped          VARCHAR(100) NOT NULL,
    cap_sped            VARCHAR(10)  NOT NULL,
    metodo_pagamento    VARCHAR(50)  NOT NULL,
    totale              DECIMAL(10,2) NOT NULL CHECK (totale >= 0),
    FOREIGN KEY (id_utente) REFERENCES utente(id)
);

-- Dettagli ordine (prezzo storico obbligatorio!)
CREATE TABLE dettaglio_ordine (
    id_ordine           INT NOT NULL,
    id_prodotto         INT NOT NULL,
    quantita            INT NOT NULL CHECK (quantita > 0),
    prezzo_acquisto     DECIMAL(10,2) NOT NULL CHECK (prezzo_acquisto >= 0),
    PRIMARY KEY (id_ordine, id_prodotto),
    FOREIGN KEY (id_ordine)   REFERENCES ordine(id),
    FOREIGN KEY (id_prodotto) REFERENCES prodotto(id)
);

-- Lista desideri
CREATE TABLE lista_desideri (
    id_utente   INT NOT NULL,
    id_prodotto INT NOT NULL,
    PRIMARY KEY (id_utente, id_prodotto),
    FOREIGN KEY (id_utente)   REFERENCES utente(id),
    FOREIGN KEY (id_prodotto) REFERENCES prodotto(id)
);

-- In produzione creare l'account amministratore attraverso provisioning sicuro.
-- Il file seed-demo.sql è OPZIONALE e va eseguito solo in ambiente di sviluppo.
