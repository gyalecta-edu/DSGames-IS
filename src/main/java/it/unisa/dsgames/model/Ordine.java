package it.unisa.dsgames.model;

import java.sql.Timestamp;
import java.util.List;

public class Ordine {

    public enum Stato { IN_ELABORAZIONE, SPEDITO, CONSEGNATO }

    private int id;
    private int idUtente;
    private Timestamp dataOrdine;
    private Stato stato;
    private String indirizzoSped;
    private String cittaSped;
    private String capSped;
    private String metodoPagamento;
    private double totale;
    private List<DettaglioOrdine> dettagli;

    public Ordine() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public Timestamp getDataOrdine() { return dataOrdine; }
    public void setDataOrdine(Timestamp dataOrdine) { this.dataOrdine = dataOrdine; }

    public Stato getStato() { return stato; }
    public void setStato(Stato stato) { this.stato = stato; }

    public String getIndirizzoSped() { return indirizzoSped; }
    public void setIndirizzoSped(String indirizzoSped) { this.indirizzoSped = indirizzoSped; }

    public String getCittaSped() { return cittaSped; }
    public void setCittaSped(String cittaSped) { this.cittaSped = cittaSped; }

    public String getCapSped() { return capSped; }
    public void setCapSped(String capSped) { this.capSped = capSped; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public double getTotale() { return totale; }
    public void setTotale(double totale) { this.totale = totale; }

    public List<DettaglioOrdine> getDettagli() { return dettagli; }
    public void setDettagli(List<DettaglioOrdine> dettagli) { this.dettagli = dettagli; }
}