package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.DettaglioOrdine;
import it.unisa.dsgames.model.Ordine;
import it.unisa.dsgames.model.Prodotto;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/** Persistence gateway: checkout atomico, validazione scorte e prezzi lato DB. */
public class OrdineDAOImpl implements OrdineDAO {
    private final DataSource dataSource;
    public OrdineDAOImpl(DataSource dataSource) { this.dataSource = dataSource; }

    private Ordine mapRow(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getInt("id"));
        o.setIdUtente(rs.getInt("id_utente"));
        o.setDataOrdine(rs.getTimestamp("data_ordine"));
        o.setStato(Ordine.Stato.valueOf(rs.getString("stato")));
        o.setIndirizzoSped(rs.getString("indirizzo_sped"));
        o.setCittaSped(rs.getString("citta_sped"));
        o.setCapSped(rs.getString("cap_sped"));
        o.setMetodoPagamento(rs.getString("metodo_pagamento"));
        o.setTotale(rs.getDouble("totale"));
        return o;
    }

    @Override
    public int doSave(Ordine ordine) throws SQLException {
        if (ordine == null || ordine.getDettagli() == null || ordine.getDettagli().isEmpty())
            throw new SQLException("Ordine senza prodotti");
        // Lock ordinato degli ID prodotto per limitare il rischio di deadlock tra checkout concorrenti.
        List<DettaglioOrdine> requested = new ArrayList<>(ordine.getDettagli());
        requested.sort(Comparator.comparingInt(DettaglioOrdine::getIdProdotto));
        String select = "SELECT prezzo, in_offerta, prezzo_offerta, quantita, eliminato " +
                "FROM prodotto WHERE id = ? FOR UPDATE";
        String reduce = "UPDATE prodotto SET quantita = quantita - ? WHERE id = ? AND quantita >= ?";
        String orderSql = "INSERT INTO ordine (id_utente, stato, indirizzo_sped, citta_sped, " +
                "cap_sped, metodo_pagamento, totale) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String detailSql = "INSERT INTO dettaglio_ordine " +
                "(id_ordine, id_prodotto, quantita, prezzo_acquisto) VALUES (?, ?, ?, ?)";

        try (Connection con = dataSource.getConnection()) {
            boolean previousAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                Set<Integer> seen = new HashSet<>();
                for (DettaglioOrdine line : requested) {
                    if (line.getIdProdotto() <= 0 || line.getQuantita() <= 0 ||
                            !seen.add(line.getIdProdotto()))
                        throw new SQLException("Prodotto o quantità non validi");
                    try (PreparedStatement lock = con.prepareStatement(select)) {
                        lock.setInt(1, line.getIdProdotto());
                        try (ResultSet rs = lock.executeQuery()) {
                            if (!rs.next() || rs.getBoolean("eliminato") ||
                                    rs.getInt("quantita") < line.getQuantita())
                                throw new SQLException("Disponibilità insufficiente per il prodotto #" + line.getIdProdotto());
                            BigDecimal standard = rs.getBigDecimal("prezzo");
                            BigDecimal offer = rs.getBigDecimal("prezzo_offerta");
                            BigDecimal price = rs.getBoolean("in_offerta") && offer != null &&
                                    offer.signum() > 0 ? offer : standard;
                            if (price == null || price.signum() < 0)
                                throw new SQLException("Prezzo prodotto non valido");
                            price = price.setScale(2, RoundingMode.HALF_UP);
                            line.setPrezzoAcquisto(price.doubleValue());
                            total = total.add(price.multiply(BigDecimal.valueOf(line.getQuantita())));
                        }
                    }
                    try (PreparedStatement decrement = con.prepareStatement(reduce)) {
                        decrement.setInt(1, line.getQuantita());
                        decrement.setInt(2, line.getIdProdotto());
                        decrement.setInt(3, line.getQuantita());
                        if (decrement.executeUpdate() != 1)
                            throw new SQLException("Scorte modificate da un altro acquisto");
                    }
                }
                int idOrder;
                try (PreparedStatement ps = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, ordine.getIdUtente());
                    ps.setString(2, Ordine.Stato.IN_ELABORAZIONE.name());
                    ps.setString(3, ordine.getIndirizzoSped());
                    ps.setString(4, ordine.getCittaSped());
                    ps.setString(5, ordine.getCapSped());
                    ps.setString(6, ordine.getMetodoPagamento());
                    ps.setBigDecimal(7, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("ID ordine non generato");
                        idOrder = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(detailSql)) {
                    for (DettaglioOrdine line : requested) {
                        ps.setInt(1, idOrder);
                        ps.setInt(2, line.getIdProdotto());
                        ps.setInt(3, line.getQuantita());
                        ps.setBigDecimal(4, BigDecimal.valueOf(line.getPrezzoAcquisto()));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                ordine.setId(idOrder);
                ordine.setTotale(total.doubleValue());
                ordine.setDettagli(requested);
                con.commit();
                return idOrder;
            } catch (SQLException | RuntimeException ex) {
                try { con.rollback(); } catch (SQLException rollback) { ex.addSuppressed(rollback); }
                throw ex;
            } finally { con.setAutoCommit(previousAutoCommit); }
        }
    }

    @Override
    public List<Ordine> doRetrieveByUtente(int idUtente) throws SQLException {
        List<Ordine> result = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM ordine WHERE id_utente = ? ORDER BY data_ordine DESC")) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    @Override
    public List<Ordine> doRetrieveAll(Timestamp dal, Timestamp al, Integer idUtente) throws SQLException {
        List<Ordine> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ordine WHERE 1=1");
        if (dal != null) sql.append(" AND data_ordine >= ?");
        if (al != null) sql.append(" AND data_ordine <= ?");
        if (idUtente != null) sql.append(" AND id_utente = ?");
        sql.append(" ORDER BY data_ordine DESC");
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;
            if (dal != null) ps.setTimestamp(index++, dal);
            if (al != null) ps.setTimestamp(index++, al);
            if (idUtente != null) ps.setInt(index++, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    @Override
    public Ordine doRetrieveById(int id) throws SQLException {
        Ordine order = null;
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM ordine WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) order = mapRow(rs);
                }
            }
            if (order == null) return null;
            List<DettaglioOrdine> details = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT d.*, p.nome, p.immagine FROM dettaglio_ordine d " +
                            "JOIN prodotto p ON d.id_prodotto = p.id WHERE d.id_ordine = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DettaglioOrdine line = new DettaglioOrdine();
                        line.setIdOrdine(id);
                        line.setIdProdotto(rs.getInt("id_prodotto"));
                        line.setQuantita(rs.getInt("quantita"));
                        line.setPrezzoAcquisto(rs.getDouble("prezzo_acquisto"));
                        Prodotto item = new Prodotto();
                        item.setId(line.getIdProdotto());
                        item.setNome(rs.getString("nome"));
                        item.setImmagine(rs.getString("immagine"));
                        line.setProdotto(item);
                        details.add(line);
                    }
                }
            }
            order.setDettagli(details);
            return order;
        }
    }

    @Override
    public void doUpdateStato(int idOrdine, Ordine.Stato stato) throws SQLException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE ordine SET stato = ? WHERE id = ?")) {
            ps.setString(1, stato.name());
            ps.setInt(2, idOrdine);
            ps.executeUpdate();
        }
    }
}
