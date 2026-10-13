package it.unisa.dsgames.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Factory per ottenere le istanze dei DAO.
 * Gestisce il DataSource JNDI configurato in context.xml.
 */
public class DAOFactory {

    private static final String JNDI_NAME = "java:comp/env/jdbc/dsgames";
    private static DataSource dataSource;

    private DAOFactory() {}

    public static synchronized DataSource getDataSource() throws NamingException {
        if (dataSource == null) {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(JNDI_NAME);
        }
        return dataSource;
    }

    public static ProdottoDAO getProdottoDAO() throws NamingException {
        return new ProdottoDAOImpl(getDataSource());
    }

    public static UtenteDAO getUtenteDAO() throws NamingException {
        return new UtenteDAOImpl(getDataSource());
    }

    public static OrdineDAO getOrdineDAO() throws NamingException {
        return new OrdineDAOImpl(getDataSource());
    }

    public static ListaDesideriDAO getListaDesideriDAO() throws NamingException {
        return new ListaDesideriDAOImpl(getDataSource());
    }
}