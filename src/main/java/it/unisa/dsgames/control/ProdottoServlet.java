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

@WebServlet("/prodotto")
public class ProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/catalogo");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            ProdottoDAO dao = DAOFactory.getProdottoDAO();
            Prodotto prodotto = dao.doRetrieveById(id);

            if (prodotto == null || prodotto.isEliminato()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                               "Prodotto non trovato.");
                return;
            }

            req.setAttribute("prodotto", prodotto);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/catalogo");
            return;
        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento del prodotto.");
        }

        req.getRequestDispatcher("/WEB-INF/view/prodotto.jsp")
           .forward(req, resp);
    }
}