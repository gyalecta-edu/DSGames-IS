package it.unisa.dsgames.dao;

import it.unisa.dsgames.model.Utente;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAOImpl implements UtenteDAO {

    private final DataSource dataSource;

    public UtenteDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Utente mapRow(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRuolo(Utente.Ruolo.valueOf(rs.getString("ruolo")));
        u.setIndirizzo(rs.getString("indirizzo"));
        u.setCitta(rs.getString("citta"));
        u.setCap(rs.getString("cap"));
        u.setTelefono(rs.getString("telefono"));
        u.setDataReg(rs.getTimestamp("data_reg"));
        return u;
    }

    @Override
    public Utente doRetrieveByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utente WHERE email = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Utente doRetrieveById(int id) throws SQLException {
        String sql = "SELECT * FROM utente WHERE id = ?";
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
    public List<Utente> doRetrieveAll() throws SQLException {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM utente ORDER BY cognome, nome";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    @Override
    public void doSave(Utente u) throws SQLException {
        String sql = "INSERT INTO utente (nome, cognome, email, password_hash, ruolo) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPasswordHash());
            ps.setString(5, u.getRuolo().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    @Override
    public void doUpdate(Utente u) throws SQLException {
        String sql = "UPDATE utente SET nome=?, cognome=?, email=?, " +
                     "indirizzo=?, citta=?, cap=?, telefono=? WHERE id=?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getIndirizzo());
            ps.setString(5, u.getCitta());
            ps.setString(6, u.getCap());
            ps.setString(7, u.getTelefono());
            ps.setInt(8, u.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void doUpdatePassword(int idUtente, String nuovoHash) throws SQLException {
    String sql = "UPDATE utente SET password_hash = ? WHERE id = ?";
    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nuovoHash);
        ps.setInt(2, idUtente);
        ps.executeUpdate();
    }
}
}