package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Ordine;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface OrdineDAO {

    /** Salva un nuovo ordine e restituisce l'ID generato */
    int doSave(Ordine ordine) throws SQLException;

    /** Recupera tutti gli ordini di un utente */
    List<Ordine> doRetrieveByUtente(int idUtente) throws SQLException;

    /** Recupera tutti gli ordini (per admin) con filtri opzionali */
    List<Ordine> doRetrieveAll(Timestamp dal, Timestamp al,
                               Integer idUtente) throws SQLException;

    /** Recupera un ordine per ID con i relativi dettagli */
    Ordine doRetrieveById(int id) throws SQLException;

    /** Aggiorna lo stato di un ordine */
    void doUpdateStato(int idOrdine, Ordine.Stato stato) throws SQLException;
}