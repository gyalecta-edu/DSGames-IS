package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.OrdineDAO;
import it.unisa.dsgames.model.Ordine;
import it.unisa.dsgames.model.Utente;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ordini")
public class OrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Controllo accesso
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=ordini");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");

        // Messaggio di conferma ordine appena completato
        String conferma = req.getParameter("conferma");
        if (conferma != null && !conferma.isEmpty()) {
            req.setAttribute("conferma",
                "Ordine #" + conferma + " completato! Grazie per il tuo acquisto.");
        }

        try {
            OrdineDAO ordineDAO = DAOFactory.getOrdineDAO();

            // Dettaglio singolo ordine
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id;
                try { id = Integer.parseInt(idStr); }
                catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine non valido");
                    return;
                }
                Ordine ordine = ordineDAO.doRetrieveById(id);

                // Sicurezza: l'utente può vedere solo i propri ordini
                if (ordine == null || ordine.getIdUtente() != utente.getId()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                req.setAttribute("ordine", ordine);
                req.getRequestDispatcher("/WEB-INF/view/dettaglioOrdine.jsp")
                   .forward(req, resp);
                return;
            }

            // Lista di tutti gli ordini dell'utente
            List<Ordine> ordini = ordineDAO.doRetrieveByUtente(utente.getId());
            req.setAttribute("ordini", ordini);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore",
                "Errore nel caricamento degli ordini.");
        }

        req.getRequestDispatcher("/WEB-INF/view/ordini.jsp")
           .forward(req, resp);
    }
}