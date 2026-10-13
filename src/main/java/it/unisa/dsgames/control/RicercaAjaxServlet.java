package it.unisa.dsgames.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

@WebServlet("/ajax/ricerca")
public class RicercaAjaxServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String q           = req.getParameter("q");
        String catStr      = req.getParameter("categoria");
        String piattaforma = req.getParameter("piattaforma");

        Prodotto.Categoria categoria = null;
        if (catStr != null && !catStr.isEmpty()) {
            try {
                categoria = Prodotto.Categoria.valueOf(catStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        JsonObject response = new JsonObject();

        try {
            ProdottoDAO dao = DAOFactory.getProdottoDAO();
            List<Prodotto> prodotti = dao.doSearch(q, piattaforma, categoria);

            JsonArray array = new JsonArray();
            for (Prodotto p : prodotti) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id",             p.getId());
                obj.addProperty("nome",           p.getNome());
                obj.addProperty("categoria",      p.getCategoria().name());
                obj.addProperty("piattaforma",    p.getPiattaforma());
                obj.addProperty("prezzo",         p.getPrezzo());
                obj.addProperty("prezzoEffettivo",p.getPrezzoEffettivo());
                obj.addProperty("inOfferta",      p.isInOfferta());
                obj.addProperty("immagine",       p.getImmagine());
                obj.addProperty("quantita",       p.getQuantita());
                array.add(obj);
            }

            response.addProperty("success", true);
            response.add("prodotti", array);
            response.addProperty("totale", prodotti.size());

        } catch (SQLException | NamingException e) {
            response.addProperty("success", false);
            response.addProperty("errore", "Errore nella ricerca.");
        }

        resp.getWriter().write(gson.toJson(response));
    }
}