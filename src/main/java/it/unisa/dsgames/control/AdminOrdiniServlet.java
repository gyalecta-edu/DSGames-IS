package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.OrdineDAO;
import it.unisa.dsgames.model.Ordine;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;



@WebServlet("/admin/ordini")
public class AdminOrdiniServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String dal        = req.getParameter("dal");
        String al         = req.getParameter("al");
        String clienteStr = req.getParameter("idCliente");

        Timestamp tsDal    = null;
        Timestamp tsAl     = null;
        Integer   idCliente = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (dal != null && !dal.isEmpty())
                tsDal = new Timestamp(sdf.parse(dal).getTime());
            if (al != null && !al.isEmpty()) {
                // Al = fine giornata
                tsAl = new Timestamp(sdf.parse(al).getTime() + 86399999L);
            }
            if (clienteStr != null && !clienteStr.isEmpty())
                idCliente = Integer.parseInt(clienteStr);

        } catch (ParseException | NumberFormatException ignored) {}

        try {
            OrdineDAO dao = DAOFactory.getOrdineDAO();

            // Dettaglio singolo ordine
            String idStr = req.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                int id;
                try { id = Integer.parseInt(idStr); }
                catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine non valido");
                    return;
                }
                Ordine ordine = dao.doRetrieveById(id);
                if (ordine == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                req.setAttribute("ordine", ordine);
                req.getRequestDispatcher("/WEB-INF/view/admin/dettaglioOrdine.jsp")
                   .forward(req, resp);
                return;
            }

            List<Ordine> ordini = dao.doRetrieveAll(tsDal, tsAl, idCliente);
            req.setAttribute("ordini",    ordini);
            req.setAttribute("filtroDal", dal);
            req.setAttribute("filtroAl",  al);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore nel caricamento ordini.");
        }

        req.getRequestDispatcher("/WEB-INF/view/admin/ordini.jsp")
           .forward(req, resp);
    }
}