package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Prodotto;
import java.sql.SQLException;
import java.util.List;

public interface ProdottoDAO {

    /** Restituisce tutti i prodotti non eliminati */
    List<Prodotto> doRetrieveAll() throws SQLException;

    /** Restituisce i prodotti filtrati per categoria */
    List<Prodotto> doRetrieveByCategoria(Prodotto.Categoria categoria) throws SQLException;

    /** Restituisce un singolo prodotto per ID (anche se eliminato, per gli ordini storici) */
    Prodotto doRetrieveById(int id) throws SQLException;

    /** Ricerca per nome, piattaforma e/o categoria */
    List<Prodotto> doSearch(String nome, String piattaforma,
                            Prodotto.Categoria categoria) throws SQLException;

    /** Restituisce i prodotti in offerta */
    List<Prodotto> doRetrieveInOfferta() throws SQLException;
    
    /** Restituisce l'elenco distinto delle piattaforme presenti nel catalogo */
    List<String> doRetrieveDistinctPiattaforme() throws SQLException;

    /** Restituisce tutti i prodotti inclusi quelli eliminati (solo per admin) */
    List<Prodotto> doRetrieveAllInclusiEliminati() throws SQLException;

    /** Inserisce un nuovo prodotto */
    void doSave(Prodotto prodotto) throws SQLException;

    /** Aggiorna un prodotto esistente */
    void doUpdate(Prodotto prodotto) throws SQLException;

    /** Soft delete: imposta eliminato=true senza cancellare dal DB */
    void doDelete(int id) throws SQLException;
}