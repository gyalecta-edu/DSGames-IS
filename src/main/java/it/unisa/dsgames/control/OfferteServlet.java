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

@WebServlet("/offerte")
public class OfferteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            ProdottoDAO dao = DAOFactory.getProdottoDAO();
            List<Prodotto> offerte = dao.doRetrieveInOfferta();
            req.setAttribute("offerte", offerte);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento delle offerte.");
        }

        req.getRequestDispatcher("/WEB-INF/view/offerte.jsp")
           .forward(req, resp);
    }
}