package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Prodotto;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListaDesideriDAOImpl implements ListaDesideriDAO {

    private final DataSource dataSource;

    public ListaDesideriDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Prodotto> doRetrieveByUtente(int idUtente) throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM prodotto p " +
                     "JOIN lista_desideri ld ON p.id = ld.id_prodotto " +
                     "WHERE ld.id_utente = ? AND p.eliminato = FALSE";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prodotto p = new Prodotto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setCategoria(Prodotto.Categoria.valueOf(rs.getString("categoria")));
                    p.setPiattaforma(rs.getString("piattaforma"));
                    p.setPrezzo(rs.getDouble("prezzo"));
                    p.setQuantita(rs.getInt("quantita"));
                    p.setImmagine(rs.getString("immagine"));
                    p.setInOfferta(rs.getBoolean("in_offerta"));
                    p.setPrezzoOfferta(rs.getDouble("prezzo_offerta"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    @Override
    public void doAdd(int idUtente, int idProdotto) throws SQLException {
        String sql = "INSERT IGNORE INTO lista_desideri (id_utente, id_prodotto) VALUES (?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            ps.executeUpdate();
        }
    }

    @Override
    public void doRemove(int idUtente, int idProdotto) throws SQLException {
        String sql = "DELETE FROM lista_desideri WHERE id_utente = ? AND id_prodotto = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean doExists(int idUtente, int idProdotto) throws SQLException {
        String sql = "SELECT 1 FROM lista_desideri WHERE id_utente = ? AND id_prodotto = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}