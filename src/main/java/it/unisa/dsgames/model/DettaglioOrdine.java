package it.unisa.dsgames.model;

public class DettaglioOrdine {

    private int idOrdine;
    private int idProdotto;
    private int quantita;
    private double prezzoAcquisto; // prezzo al momento dell'acquisto, mai modificato
    private Prodotto prodotto;     // oggetto prodotto associato (per la vista)

    public DettaglioOrdine() {}

    public DettaglioOrdine(int idOrdine, int idProdotto, int quantita, double prezzoAcquisto) {
        this.idOrdine = idOrdine;
        this.idProdotto = idProdotto;
        this.quantita = quantita;
        this.prezzoAcquisto = prezzoAcquisto;
    }

    public int getIdOrdine() { return idOrdine; }
    public void setIdOrdine(int idOrdine) { this.idOrdine = idOrdine; }

    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public double getPrezzoAcquisto() { return prezzoAcquisto; }
    public void setPrezzoAcquisto(double prezzoAcquisto) { this.prezzoAcquisto = prezzoAcquisto; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public double getSubtotale() { return prezzoAcquisto * quantita; }
}