package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Prodotto;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAOImpl implements ProdottoDAO {

    private final DataSource dataSource;

    public ProdottoDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Mappa una riga del ResultSet in un oggetto Prodotto
    private Prodotto mapRow(ResultSet rs) throws SQLException {
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
        p.setEliminato(rs.getBoolean("eliminato"));
        return p;
    }

    @Override
    public List<Prodotto> doRetrieveAll() throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        String sql = "SELECT * FROM prodotto WHERE eliminato = FALSE ORDER BY nome";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    @Override
    public List<Prodotto> doRetrieveByCategoria(Prodotto.Categoria categoria) throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        String sql = "SELECT * FROM prodotto WHERE eliminato = FALSE AND categoria = ? ORDER BY nome";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public Prodotto doRetrieveById(int id) throws SQLException {
        String sql = "SELECT * FROM prodotto WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Prodotto> doSearch(String nome, String piattaforma,
                                   Prodotto.Categoria categoria) throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM prodotto WHERE eliminato = FALSE");
        if (nome != null && !nome.isEmpty())
            sql.append(" AND nome LIKE ?");
        if (piattaforma != null && !piattaforma.isEmpty())
            sql.append(" AND piattaforma = ?");
        if (categoria != null)
            sql.append(" AND categoria = ?");
        sql.append(" ORDER BY nome");

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (nome != null && !nome.isEmpty())
                ps.setString(idx++, "%" + nome + "%");
            if (piattaforma != null && !piattaforma.isEmpty())
                ps.setString(idx++, piattaforma);
            if (categoria != null)
                ps.setString(idx, categoria.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Prodotto> doRetrieveInOfferta() throws SQLException {
        List<Prodotto> lista = new ArrayList<>();
        String sql = "SELECT * FROM prodotto WHERE eliminato = FALSE AND in_offerta = TRUE ORDER BY nome";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    @Override
    public void doSave(Prodotto p) throws SQLException {
        String sql = "INSERT INTO prodotto (nome, descrizione, categoria, piattaforma, " +
                     "prezzo, quantita, immagine, in_offerta, prezzo_offerta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setString(3, p.getCategoria().name());
            ps.setString(4, p.getPiattaforma());
            ps.setDouble(5, p.getPrezzo());
            ps.setInt(6, p.getQuantita());
            ps.setString(7, p.getImmagine());
            ps.setBoolean(8, p.isInOfferta());
            ps.setDouble(9, p.getPrezzoOfferta());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void doUpdate(Prodotto p) throws SQLException {
        String sql = "UPDATE prodotto SET nome=?, descrizione=?, categoria=?, piattaforma=?, " +
                     "prezzo=?, quantita=?, immagine=?, in_offerta=?, prezzo_offerta=? WHERE id=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setString(3, p.getCategoria().name());
            ps.setString(4, p.getPiattaforma());
            ps.setDouble(5, p.getPrezzo());
            ps.setInt(6, p.getQuantita());
            ps.setString(7, p.getImmagine());
            ps.setBoolean(8, p.isInOfferta());
            ps.setDouble(9, p.getPrezzoOfferta());
            ps.setInt(10, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void doDelete(int id) throws SQLException {
        String sql = "UPDATE prodotto SET eliminato = TRUE WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    @Override
    public List<String> doRetrieveDistinctPiattaforme() throws SQLException {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT piattaforma FROM prodotto " +
                     "WHERE eliminato = FALSE AND piattaforma IS NOT NULL " +
                     "ORDER BY piattaforma";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("piattaforma"));
        }
        return lista;
    }

    @Override
    public List<Prodotto> doRetrieveAllInclusiEliminati() throws SQLException {
    List<Prodotto> lista = new ArrayList<>();
    String sql = "SELECT * FROM prodotto ORDER BY eliminato ASC, nome ASC";
    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) lista.add(mapRow(rs));
    }
    return lista;
}
}