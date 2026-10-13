package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.ProdottoDAO;
import it.unisa.dsgames.model.Prodotto;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            ProdottoDAO prodottoDAO = DAOFactory.getProdottoDAO();

            List<Prodotto> prodotti = prodottoDAO.doRetrieveAll();
            List<Prodotto> offerte  = prodottoDAO.doRetrieveInOfferta();

            req.setAttribute("prodotti", prodotti);
            req.setAttribute("offerte",  offerte);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento del catalogo.");
        }

        req.getRequestDispatcher("/WEB-INF/view/home.jsp")
           .forward(req, resp);
    }
}