package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Prodotto;
import java.sql.SQLException;
import java.util.List;

public interface ListaDesideriDAO {
    List<Prodotto> doRetrieveByUtente(int idUtente) throws SQLException;
    void doAdd(int idUtente, int idProdotto) throws SQLException;
    void doRemove(int idUtente, int idProdotto) throws SQLException;
    boolean doExists(int idUtente, int idProdotto) throws SQLException;
}