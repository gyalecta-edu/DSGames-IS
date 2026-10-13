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

@WebServlet("/admin/prodotti")
public class AdminProdottiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");
        if (azione == null) azione = "lista";

        try {
            ProdottoDAO dao = DAOFactory.getProdottoDAO();

            switch (azione) {

                case "nuovo":
                    // Mostra form prodotto vuoto
                    req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp")
                       .forward(req, resp);
                    return;

                case "modifica": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Prodotto p = dao.doRetrieveById(id);
                    if (p == null) {
                        resp.sendRedirect(req.getContextPath() + "/admin/prodotti");
                        return;
                    }
                    req.setAttribute("prodotto", p);
                    req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp")
                       .forward(req, resp);
                    return;
                }


                default: {
                    // Lista tutti i prodotti (inclusi eliminati per l'admin)
                    List<Prodotto> prodotti = dao.doRetrieveAllInclusiEliminati();
                    req.setAttribute("prodotti", prodotti);
                    String messaggio = req.getParameter("messaggio");
                    if (messaggio != null)
                        req.setAttribute("messaggio", messaggio);
                    req.getRequestDispatcher("/WEB-INF/view/admin/prodotti.jsp")
                       .forward(req, resp);
                }
            }

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/admin/prodotti.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr     = req.getParameter("id");
        if ("elimina".equals(req.getParameter("azione"))) {
            try {
                int id = Integer.parseInt(idStr);
                if (id <= 0) throw new NumberFormatException();
                DAOFactory.getProdottoDAO().doDelete(id);
                resp.sendRedirect(req.getContextPath() + "/admin/prodotti?messaggio=Prodotto+eliminato+correttamente.");
            } catch (IllegalArgumentException e) { resp.sendError(400, "ID prodotto non valido"); }
              catch (SQLException | NamingException e) { throw new ServletException("Eliminazione fallita", e); }
            return;
        }

        String nome      = req.getParameter("nome");
        String descr     = req.getParameter("descrizione");
        String catStr    = req.getParameter("categoria");
        String piatt     = req.getParameter("piattaforma");
        String prezzoStr = req.getParameter("prezzo");
        String qtStr     = req.getParameter("quantita");
        String immagine  = req.getParameter("immagine");
        String offerta   = req.getParameter("inOfferta");
        String prezzoOff = req.getParameter("prezzoOfferta");

        // Validazione server-side
        if (nome == null || nome.trim().isEmpty() ||
            prezzoStr == null || prezzoStr.trim().isEmpty() ||
            catStr == null    || catStr.trim().isEmpty()) {

            req.setAttribute("errore",
                "Nome, categoria e prezzo sono obbligatori.");
            req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp")
               .forward(req, resp);
            return;
        }

        try {
            Prodotto p = new Prodotto();
            p.setNome(nome.trim());
            p.setDescrizione(descr != null ? descr.trim() : "");
            p.setCategoria(Prodotto.Categoria.valueOf(catStr));
            p.setPiattaforma(piatt != null ? piatt.trim() : "");
            p.setPrezzo(Double.parseDouble(prezzoStr.replace(",", ".")));
            p.setQuantita(qtStr != null && !qtStr.isEmpty()
                ? Integer.parseInt(qtStr) : 0);
            p.setImmagine(immagine != null ? immagine.trim() : "");
            p.setInOfferta("on".equals(offerta));
            p.setPrezzoOfferta(
                prezzoOff != null && !prezzoOff.isEmpty()
                    ? Double.parseDouble(prezzoOff.replace(",", ".")) : 0);
            if (!Double.isFinite(p.getPrezzo()) || p.getPrezzo() <= 0 || p.getQuantita() < 0 ||
                    !Double.isFinite(p.getPrezzoOfferta()) ||
                    (p.isInOfferta() && (p.getPrezzoOfferta() <= 0 ||
                        p.getPrezzoOfferta() >= p.getPrezzo()))) {
                req.setAttribute("errore", "Prezzo, sconto o quantità non validi.");
                req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp").forward(req, resp);
                return;
            }

            ProdottoDAO dao = DAOFactory.getProdottoDAO();

            if (idStr != null && !idStr.isEmpty()) {
                // Modifica prodotto esistente
                p.setId(Integer.parseInt(idStr));
                dao.doUpdate(p);
                resp.sendRedirect(req.getContextPath() +
                    "/admin/prodotti?messaggio=Prodotto+modificato+correttamente.");
            } else {
                // Nuovo prodotto
                dao.doSave(p);
                resp.sendRedirect(req.getContextPath() +
                    "/admin/prodotti?messaggio=Prodotto+aggiunto+correttamente.");
            }

        } catch (IllegalArgumentException e) {
            req.setAttribute("errore",
                "Prezzo o quantità non validi.");
            req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp")
               .forward(req, resp);
        } catch (SQLException | NamingException e) {
            req.setAttribute("errore",
                "Errore nel salvataggio: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/admin/formProdotto.jsp")
               .forward(req, resp);
        }
    }
}