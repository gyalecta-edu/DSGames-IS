package it.unisa.dsgames.control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.ListaDesideriDAO;
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

@WebServlet("/ajax/wishlist")
public class WishlistAjaxServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject response = new JsonObject();
        HttpSession session = req.getSession(false);

        // Solo per utenti loggati
        if (session == null || session.getAttribute("utente") == null) {
            response.addProperty("success",   false);
            response.addProperty("loginRequired", true);
            response.addProperty("messaggio", "Devi accedere per usare la lista desideri.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String idStr  = req.getParameter("idProdotto");
        String azione = req.getParameter("azione");

        if (idStr == null || azione == null) {
            response.addProperty("success",  false);
            response.addProperty("messaggio","Parametri mancanti.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        try {
            int idProdotto = Integer.parseInt(idStr);
            ListaDesideriDAO dao = DAOFactory.getListaDesideriDAO();

            switch (azione) {
                case "aggiungi":
                    it.unisa.dsgames.model.Prodotto product = DAOFactory.getProdottoDAO().doRetrieveById(idProdotto);
                    if (product == null || product.isEliminato()) {
                        response.addProperty("success", false);
                        response.addProperty("messaggio", "Prodotto non disponibile.");
                        break;
                    }
                    dao.doAdd(utente.getId(), idProdotto);
                    response.addProperty("success",  true);
                    response.addProperty("inWishlist", true);
                    response.addProperty("messaggio","Aggiunto alla lista desideri!");
                    break;
                case "rimuovi":
                    dao.doRemove(utente.getId(), idProdotto);
                    response.addProperty("success",  true);
                    response.addProperty("inWishlist", false);
                    response.addProperty("messaggio","Rimosso dalla lista desideri.");
                    break;
                default:
                    response.addProperty("success",  false);
                    response.addProperty("messaggio","Azione non valida.");
            }

        } catch (NumberFormatException e) {
            response.addProperty("success",  false);
            response.addProperty("messaggio","ID non valido.");
        } catch (SQLException | NamingException e) {
            response.addProperty("success",  false);
            response.addProperty("messaggio","Errore del server.");
        }

        resp.getWriter().write(gson.toJson(response));
    }
}