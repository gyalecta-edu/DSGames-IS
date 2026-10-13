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

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Legge i parametri di filtro dalla request
        String q           = req.getParameter("q");
        String categoriaStr = req.getParameter("categoria");
        String piattaforma  = req.getParameter("piattaforma");

        // Converte la stringa categoria in enum (null se assente o non valida)
        Prodotto.Categoria categoria = null;
        if (categoriaStr != null && !categoriaStr.isEmpty()) {
            try {
                categoria = Prodotto.Categoria.valueOf(categoriaStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        try {
            ProdottoDAO dao = DAOFactory.getProdottoDAO();

            // Ricerca con filtri (il metodo gestisce i null internamente)
            List<Prodotto> prodotti = dao.doSearch(q, piattaforma, categoria);

            // Piattaforme disponibili per il pannello filtri
            List<String> piattaforme = dao.doRetrieveDistinctPiattaforme();

            req.setAttribute("prodotti",    prodotti);
            req.setAttribute("piattaforme", piattaforme);

            // Rimanda i filtri alla JSP per mantenere lo stato del form
            req.setAttribute("filtroQ",           q);
            req.setAttribute("filtroCategoria",   categoriaStr);
            req.setAttribute("filtroPiattaforma", piattaforma);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento del catalogo.");
        }

        req.getRequestDispatcher("/WEB-INF/view/catalogo.jsp")
           .forward(req, resp);
    }
}