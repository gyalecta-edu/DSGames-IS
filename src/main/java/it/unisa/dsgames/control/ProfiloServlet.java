package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.UtenteDAO;
import it.unisa.dsgames.model.Utente;
import it.unisa.dsgames.util.PasswordUtil;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/profilo")
public class ProfiloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=profilo");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/view/profilo.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("utente") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Controllo token
        String token = req.getParameter("token");
        if (token == null || !token.equals(session.getAttribute("token"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");

        String nome      = req.getParameter("nome");
        String cognome   = req.getParameter("cognome");
        String indirizzo = req.getParameter("indirizzo");
        String citta     = req.getParameter("citta");
        String cap       = req.getParameter("cap");
        String telefono  = req.getParameter("telefono");
        String nuovaPass = req.getParameter("nuovaPassword");
        String vecchiaPass = req.getParameter("vecchiaPassword");

        // Validazione base
        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty()) {
            req.setAttribute("errore", "Nome e cognome sono obbligatori.");
            req.getRequestDispatcher("/WEB-INF/view/profilo.jsp")
               .forward(req, resp);
            return;
        }

        if (nuovaPass != null && !nuovaPass.isEmpty()) {
            if (vecchiaPass == null || !PasswordUtil.verify(vecchiaPass, utente.getPasswordHash())) {
                req.setAttribute("errore", "La password attuale non è corretta.");
                req.getRequestDispatcher("/WEB-INF/view/profilo.jsp").forward(req, resp);
                return;
            }
            if (nuovaPass.length() < 8) {
                req.setAttribute("errore", "La nuova password deve contenere almeno 8 caratteri.");
                req.getRequestDispatcher("/WEB-INF/view/profilo.jsp").forward(req, resp);
                return;
            }
        }
        if ((cap != null && !cap.isBlank() && !cap.matches("\\d{5}")) ||
                nome.trim().length() > 50 || cognome.trim().length() > 50) {
            req.setAttribute("errore", "Dati del profilo non validi.");
            req.getRequestDispatcher("/WEB-INF/view/profilo.jsp").forward(req, resp);
            return;
        }
        try {
            UtenteDAO utenteDAO = DAOFactory.getUtenteDAO();
            Utente updated = new Utente();
            updated.setId(utente.getId());
            updated.setEmail(utente.getEmail());
            updated.setNome(nome.trim());
            updated.setCognome(cognome.trim());
            updated.setIndirizzo(indirizzo == null ? "" : indirizzo.trim());
            updated.setCitta(citta == null ? "" : citta.trim());
            updated.setCap(cap == null ? "" : cap.trim());
            updated.setTelefono(telefono == null ? "" : telefono.trim());
            utenteDAO.doUpdate(updated);
            if (nuovaPass != null && !nuovaPass.isEmpty()) {
                String nuovoHash = PasswordUtil.hash(nuovaPass);
                utenteDAO.doUpdatePassword(utente.getId(), nuovoHash);
                utente.setPasswordHash(nuovoHash);
            }
            utente.setNome(updated.getNome());
            utente.setCognome(updated.getCognome());
            utente.setIndirizzo(updated.getIndirizzo());
            utente.setCitta(updated.getCitta());
            utente.setCap(updated.getCap());
            utente.setTelefono(updated.getTelefono());
            session.setAttribute("utente", utente);
            req.setAttribute("successo", "Profilo aggiornato con successo.");

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore",
                "Errore durante il salvataggio del profilo.");
        }

        req.getRequestDispatcher("/WEB-INF/view/profilo.jsp")
           .forward(req, resp);
    }
}