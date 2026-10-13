package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Utente;
import java.sql.SQLException;
import java.util.List;

public interface UtenteDAO {

    /** Cerca un utente per email (usato nel login) */
    Utente doRetrieveByEmail(String email) throws SQLException;

    /** Cerca un utente per ID */
    Utente doRetrieveById(int id) throws SQLException;

    /** Restituisce tutti gli utenti (per l'admin) */
    List<Utente> doRetrieveAll() throws SQLException;

    /** Registra un nuovo utente */
    void doSave(Utente utente) throws SQLException;

    /** Aggiorna i dati di un utente */
    void doUpdate(Utente utente) throws SQLException;

    /** Aggiorna solo la password di un utente */
    void doUpdatePassword(int idUtente, String nuovoHash) throws SQLException;
}