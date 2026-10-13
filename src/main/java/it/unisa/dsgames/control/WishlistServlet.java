package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.ListaDesideriDAO;
import it.unisa.dsgames.model.Prodotto;
import it.unisa.dsgames.model.Utente;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utente user = user(req, resp);
        if (user == null) return;
        try {
            List<Prodotto> favorites = DAOFactory.getListaDesideriDAO().doRetrieveByUtente(user.getId());
            req.setAttribute("listaDesideri", favorites);
        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento della lista desideri.");
        }
        req.getRequestDispatcher("/WEB-INF/view/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Utente user = user(req, resp);
        if (user == null) return;
        try {
            int id = Integer.parseInt(req.getParameter("idProdotto"));
            if (id <= 0) throw new NumberFormatException();
            ListaDesideriDAO dao = DAOFactory.getListaDesideriDAO();
            if ("rimuovi".equals(req.getParameter("azione"))) dao.doRemove(user.getId(), id);
            else if ("aggiungi".equals(req.getParameter("azione"))) {
                Prodotto product = DAOFactory.getProdottoDAO().doRetrieveById(id);
                if (product == null || product.isEliminato()) throw new IllegalArgumentException();
                dao.doAdd(user.getId(), id);
            } else { resp.sendError(400); return; }
        } catch (IllegalArgumentException e) {
            resp.sendError(400, "Prodotto non valido");
            return;
        } catch (SQLException | NamingException e) {
            throw new ServletException("Aggiornamento wishlist fallito", e);
        }
        resp.sendRedirect(req.getContextPath() + "/wishlist");
    }

    private Utente user(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute("utente") instanceof Utente)) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=wishlist");
            return null;
        }
        return (Utente) session.getAttribute("utente");
    }
}
