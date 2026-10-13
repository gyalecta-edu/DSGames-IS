package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.OrdineDAO;
import it.unisa.dsgames.model.DettaglioOrdine;
import it.unisa.dsgames.model.ItemCarrello;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Controllo accesso: solo utenti loggati
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=checkout");
            return;
        }

        // Carrello vuoto: torna al catalogo
        Map<?, ?> carrello = (Map<?, ?>) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/carrello");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/view/checkout.jsp")
           .forward(req, resp);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Controllo accesso
        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=checkout");
            return;
        }

        // Controllo token CSRF
        String token = req.getParameter("token");
        if (token == null || !token.equals(session.getAttribute("token"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token non valido.");
            return;
        }

        Map<Integer, ItemCarrello> carrello =
            (Map<Integer, ItemCarrello>) session.getAttribute("carrello");

        if (carrello == null || carrello.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/carrello");
            return;
        }

        // Legge i dati del form
        String indirizzo = req.getParameter("indirizzo");
        String citta     = req.getParameter("citta");
        String cap       = req.getParameter("cap");
        String pagamento = req.getParameter("metodoPagamento");

        // Validazione server-side
        if (indirizzo == null || indirizzo.trim().isEmpty() ||
            citta == null     || citta.trim().isEmpty()     ||
            cap == null       || cap.trim().isEmpty()       ||
            pagamento == null || pagamento.trim().isEmpty()) {

            req.setAttribute("errore",
                "Compila tutti i campi obbligatori.");
            req.getRequestDispatcher("/WEB-INF/view/checkout.jsp")
               .forward(req, resp);
            return;
        }

        if (!cap.trim().matches("\\d{5}")) {
            req.setAttribute("errore", "Il CAP deve essere composto da 5 cifre.");
            req.getRequestDispatcher("/WEB-INF/view/checkout.jsp")
               .forward(req, resp);
            return;
        }

        if (indirizzo.trim().length() > 200 || citta.trim().length() > 100 ||
                !java.util.Arrays.asList("Carta di credito", "PayPal", "Bonifico bancario", "Contrassegno")
                        .contains(pagamento.trim())) {
            req.setAttribute("errore", "Indirizzo o metodo di pagamento non valido.");
            req.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(req, resp);
            return;
        }

        try {
            Utente utente = (Utente) session.getAttribute("utente");

            // Costruisce l'oggetto Ordine
            Ordine ordine = new Ordine();
            ordine.setIdUtente(utente.getId());
            ordine.setIndirizzoSped(indirizzo.trim());
            ordine.setCittaSped(citta.trim());
            ordine.setCapSped(cap.trim());
            ordine.setMetodoPagamento(pagamento.trim());

            // Costruisce i dettagli e calcola il totale
            List<DettaglioOrdine> dettagli = new ArrayList<>();
            double totale = 0;

            for (ItemCarrello item : carrello.values()) {
                DettaglioOrdine d = new DettaglioOrdine();
                d.setIdProdotto(item.getProdotto().getId());
                d.setQuantita(item.getQuantita());
                // Prezzo al momento dell'acquisto (storico)
                d.setPrezzoAcquisto(item.getProdotto().getPrezzoEffettivo());
                dettagli.add(d);
                totale += item.getSubtotale();
            }

            ordine.setDettagli(dettagli);
            ordine.setTotale(totale);

            // Salva l'ordine nel DB (transazione atomica)
            OrdineDAO ordineDAO = DAOFactory.getOrdineDAO();
            int idOrdine = ordineDAO.doSave(ordine);

            // Svuota il carrello
            carrello.clear();
            session.setAttribute("carrello", carrello);

            // Redirect alla conferma
            resp.sendRedirect(req.getContextPath() +
                "/ordini?conferma=" + idOrdine);

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", (e instanceof SQLException &&
                e.getMessage() != null && (e.getMessage().contains("Disponibilità") ||
                e.getMessage().contains("Scorte")))
                ? "Disponibilità modificata: aggiorna il carrello e riprova."
                : "Errore durante il completamento dell'ordine. Riprova.");
            req.getRequestDispatcher("/WEB-INF/view/checkout.jsp")
               .forward(req, resp);
        }
    }
}