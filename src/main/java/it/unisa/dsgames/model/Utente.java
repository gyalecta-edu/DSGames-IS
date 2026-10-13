package it.unisa.dsgames.model;

import java.sql.Timestamp;

public class Utente {

    public enum Ruolo { USER, ADMIN }

    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String passwordHash;
    private Ruolo ruolo;
    private String indirizzo;
    private String citta;
    private String cap;
    private String telefono;
    private Timestamp dataReg;

    public Utente() {}

    public Utente(int id, String nome, String cognome, String email,
                  String passwordHash, Ruolo ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
        this.ruolo = ruolo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public String getCap() { return cap; }
    public void setCap(String cap) { this.cap = cap; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Timestamp getDataReg() { return dataReg; }
    public void setDataReg(Timestamp dataReg) { this.dataReg = dataReg; }

    public boolean isAdmin() { return Ruolo.ADMIN.equals(this.ruolo); }
}