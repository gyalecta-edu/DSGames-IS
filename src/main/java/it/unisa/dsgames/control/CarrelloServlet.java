package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.model.ItemCarrello;
import it.unisa.dsgames.model.Prodotto;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** GET visualizza soltanto. Ogni modifica avviene via POST + token CSRF. */
@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {
    @SuppressWarnings("unchecked")
    private static Map<Integer, ItemCarrello> cart(HttpSession session) {
        Map<Integer, ItemCarrello> result = (Map<Integer, ItemCarrello>) session.getAttribute("carrello");
        if (result == null) {
            result = new LinkedHashMap<>();
            session.setAttribute("carrello", result);
        }
        return result;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<Integer, ItemCarrello> current = cart(req.getSession());
        String message = (String) req.getSession().getAttribute("messaggioCarrello");
        if (message != null) {
            req.setAttribute("errore", message);
            req.getSession().removeAttribute("messaggioCarrello");
        }
        req.setAttribute("carrello", current);
        req.setAttribute("totale", current.values().stream().mapToDouble(ItemCarrello::getSubtotale).sum());
        req.getRequestDispatcher("/WEB-INF/view/carrello.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Map<Integer, ItemCarrello> current = cart(session);
        String action = req.getParameter("azione");
        try {
            if ("svuota".equals(action)) {
                current.clear();
            } else if ("aggiungi".equals(action)) {
                int id = positiveId(req.getParameter("idProdotto"));
                Prodotto p = DAOFactory.getProdottoDAO().doRetrieveById(id);
                if (p == null || p.isEliminato() || p.getQuantita() <= 0)
                    throw new IllegalArgumentException("Prodotto non disponibile");
                ItemCarrello item = current.get(id);
                if (item == null) current.put(id, new ItemCarrello(p, 1));
                else if (item.getQuantita() < p.getQuantita()) {
                    item.setProdotto(p);
                    item.setQuantita(item.getQuantita() + 1);
                } else throw new IllegalArgumentException("Disponibilità insufficiente");
            } else if ("rimuovi".equals(action)) {
                current.remove(positiveId(req.getParameter("idProdotto")));
            } else if (action == null || "aggiorna".equals(action)) {
                String[] ids = req.getParameterValues("idProdotto");
                String[] quantities = req.getParameterValues("quantita");
                if (ids == null || quantities == null || ids.length != quantities.length)
                    throw new IllegalArgumentException("Quantità non valide");
                for (int i = 0; i < ids.length; i++) {
                    int id = positiveId(ids[i]);
                    int qty = Integer.parseInt(quantities[i]);
                    if (!current.containsKey(id)) continue;
                    if (qty <= 0) { current.remove(id); continue; }
                    Prodotto p = DAOFactory.getProdottoDAO().doRetrieveById(id);
                    if (p == null || p.isEliminato() || p.getQuantita() == 0) {
                        current.remove(id);
                        continue;
                    }
                    current.get(id).setProdotto(p);
                    current.get(id).setQuantita(Math.min(qty, p.getQuantita()));
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non supportata");
                return;
            }
        } catch (SQLException | NamingException e) {
            throw new ServletException("Impossibile aggiornare il carrello", e);
        } catch (IllegalArgumentException e) {
            // Gli errori di disponibilità/parametri non alterano il carrello.
            session.setAttribute("messaggioCarrello", "Operazione non valida o prodotto non disponibile.");
        }
        session.setAttribute("carrello", current);
        resp.sendRedirect(req.getContextPath() + "/carrello");
    }

    private static int positiveId(String value) {
        int id = Integer.parseInt(value);
        if (id <= 0) throw new IllegalArgumentException("ID non valido");
        return id;
    }
}
