package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.DettaglioOrdine;
import it.unisa.dsgames.model.Ordine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OrdineDAOImplTest {
    private DataSource db;
    private OrdineDAOImpl dao;

    @BeforeEach void setup() throws SQLException {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:checkout_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        db = source;
        dao = new OrdineDAOImpl(db);
        try (Connection con = db.getConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE TABLE prodotto (id INT PRIMARY KEY, prezzo DECIMAL(10,2), " +
                "in_offerta BOOLEAN, prezzo_offerta DECIMAL(10,2), quantita INT, eliminato BOOLEAN)");
            st.execute("CREATE TABLE ordine (id INT AUTO_INCREMENT PRIMARY KEY, id_utente INT, " +
                "stato VARCHAR(32), indirizzo_sped VARCHAR(200), citta_sped VARCHAR(100), " +
                "cap_sped VARCHAR(10), metodo_pagamento VARCHAR(50), totale DECIMAL(10,2), " +
                "data_ordine TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            st.execute("CREATE TABLE dettaglio_ordine (id_ordine INT, id_prodotto INT, " +
                "quantita INT, prezzo_acquisto DECIMAL(10,2))");
            st.execute("INSERT INTO prodotto VALUES (1, 99.99, TRUE, 79.95, 3, FALSE)");
        }
    }

    private Ordine order(int quantity) {
        Ordine o = new Ordine();
        o.setIdUtente(7);
        o.setIndirizzoSped("Via Roma 1");
        o.setCittaSped("Salerno");
        o.setCapSped("84100");
        o.setMetodoPagamento("Contrassegno");
        DettaglioOrdine d = new DettaglioOrdine();
        d.setIdProdotto(1);
        d.setQuantita(quantity);
        d.setPrezzoAcquisto(1.0); // Prezzo alterato dal client: DAO deve ignorarlo
        o.setDettagli(List.of(d));
        o.setTotale(1.0);
        return o;
    }

    private int stock() throws SQLException {
        try (Connection con = db.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT quantita FROM prodotto WHERE id=1")) {
            assertTrue(rs.next());
            return rs.getInt(1);
        }
    }

    @Test void orderDebitsStockAndRecalculatesPriceFromDatabase() throws SQLException {
        Ordine order = order(2);
        int id = dao.doSave(order);
        assertTrue(id > 0);
        assertEquals(1, stock());
        assertEquals(159.90, order.getTotale(), 0.0001);
        assertEquals(79.95, order.getDettagli().get(0).getPrezzoAcquisto(), 0.0001);
        assertEquals(159.90, dao.doRetrieveById(id).getTotale(), 0.0001);
    }

    @Test void insufficientStockRollsBackEntireTransaction() throws SQLException {
        assertThrows(SQLException.class, () -> dao.doSave(order(4)));
        assertEquals(3, stock());
        try (Connection con = db.getConnection(); Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM ordine")) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }
}
