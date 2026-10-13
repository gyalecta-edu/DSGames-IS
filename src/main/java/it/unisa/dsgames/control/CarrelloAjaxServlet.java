package it.unisa.dsgames.control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.ProdottoDAO;
import it.unisa.dsgames.model.ItemCarrello;
import it.unisa.dsgames.model.Prodotto;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/ajax/carrello")
public class CarrelloAjaxServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @SuppressWarnings("unchecked")
    private Map<Integer, ItemCarrello> getCarrello(HttpSession session) {
        Map<Integer, ItemCarrello> carrello =
            (Map<Integer, ItemCarrello>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new LinkedHashMap<>();
            session.setAttribute("carrello", carrello);
        }
        return carrello;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonObject response = new JsonObject();
        String idStr  = req.getParameter("idProdotto");
        String azione = req.getParameter("azione");

        if (idStr == null || azione == null) {
            response.addProperty("success", false);
            response.addProperty("messaggio", "Parametri mancanti.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            HttpSession session = req.getSession();
            Map<Integer, ItemCarrello> carrello = getCarrello(session);

            ProdottoDAO dao = DAOFactory.getProdottoDAO();
            Prodotto prodotto = dao.doRetrieveById(id);

            if (prodotto == null || prodotto.isEliminato()) {
                response.addProperty("success", false);
                response.addProperty("messaggio", "Prodotto non trovato.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            switch (azione) {
                case "aggiungi":
                    if (prodotto.getQuantita() <= 0) {
                        response.addProperty("success", false);
                        response.addProperty("messaggio", "Prodotto esaurito.");
                    } else {
                        if (carrello.containsKey(id)) {
                            ItemCarrello item = carrello.get(id);
                            int nuovaQt = item.getQuantita() + 1;
                            if (nuovaQt <= prodotto.getQuantita()) {
                                item.setQuantita(nuovaQt);
                            }
                        } else {
                            carrello.put(id, new ItemCarrello(prodotto, 1));
                        }
                        session.setAttribute("carrello", carrello);
                        response.addProperty("success", true);
                        response.addProperty("messaggio",
                            prodotto.getNome() + " aggiunto al carrello!");
                        response.addProperty("cartSize", carrello.size());
                    }
                    break;

                case "rimuovi":
                    carrello.remove(id);
                    session.setAttribute("carrello", carrello);
                    response.addProperty("success", true);
                    response.addProperty("messaggio", "Prodotto rimosso.");
                    response.addProperty("cartSize", carrello.size());
                    break;

                default:
                    response.addProperty("success", false);
                    response.addProperty("messaggio", "Azione non valida.");
            }

        } catch (NumberFormatException e) {
            response.addProperty("success", false);
            response.addProperty("messaggio", "ID prodotto non valido.");
        } catch (SQLException | NamingException e) {
            response.addProperty("success", false);
            response.addProperty("messaggio", "Errore del server.");
        }

        resp.getWriter().write(gson.toJson(response));
    }
}