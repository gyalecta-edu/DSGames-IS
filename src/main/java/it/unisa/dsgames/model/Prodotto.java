package it.unisa.dsgames.model;

public class Prodotto {

    public enum Categoria { CONSOLE, VIDEOGIOCO }

    private int id;
    private String nome;
    private String descrizione;
    private Categoria categoria;
    private String piattaforma;
    private double prezzo;
    private int quantita;
    private String immagine;
    private boolean inOfferta;
    private double prezzoOfferta;
    private boolean eliminato;

    public Prodotto() {}

    public Prodotto(int id, String nome, String descrizione, Categoria categoria,
                    String piattaforma, double prezzo, int quantita, String immagine) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.piattaforma = piattaforma;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.immagine = immagine;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getPiattaforma() { return piattaforma; }
    public void setPiattaforma(String piattaforma) { this.piattaforma = piattaforma; }

    public double getPrezzo() { return prezzo; }
    public void setPrezzo(double prezzo) { this.prezzo = prezzo; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    public boolean isInOfferta() { return inOfferta; }
    public void setInOfferta(boolean inOfferta) { this.inOfferta = inOfferta; }

    public double getPrezzoOfferta() { return prezzoOfferta; }
    public void setPrezzoOfferta(double prezzoOfferta) { this.prezzoOfferta = prezzoOfferta; }

    public boolean isEliminato() { return eliminato; }
    public void setEliminato(boolean eliminato) { this.eliminato = eliminato; }

    // Restituisce il prezzo effettivo (offerta se disponibile)
    public double getPrezzoEffettivo() {
        return (inOfferta && prezzoOfferta > 0) ? prezzoOfferta : prezzo;
    }
}