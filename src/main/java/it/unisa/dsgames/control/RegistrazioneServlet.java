package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.UtenteDAO;
import it.unisa.dsgames.model.Utente;
import it.unisa.dsgames.util.PasswordUtil;
import it.unisa.dsgames.util.CsrfToken;
import javax.servlet.http.HttpSession;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String nome     = req.getParameter("nome");
        String cognome  = req.getParameter("cognome");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");
        String conferma = req.getParameter("confermaPassword");

        // Validazione server-side
        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {

            req.setAttribute("errore", "Tutti i campi obbligatori devono essere compilati.");
            req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
               .forward(req, resp);
            return;
        }

        if (!password.equals(conferma)) {
            req.setAttribute("errore", "Le password non coincidono.");
            req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
               .forward(req, resp);
            return;
        }

        if (password.length() < 8) {
            req.setAttribute("errore", "La password deve contenere almeno 8 caratteri.");
            req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
               .forward(req, resp);
            return;
        }

        try {
            UtenteDAO utenteDAO = DAOFactory.getUtenteDAO();

            // Controlla se l'email è già registrata
            if (utenteDAO.doRetrieveByEmail(email.trim().toLowerCase()) != null) {
                req.setAttribute("errore", "Email già registrata. Prova ad accedere.");
                req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
                   .forward(req, resp);
                return;
            }

            if (!email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") ||
                nome.trim().length() > 50 || cognome.trim().length() > 50) {
                req.setAttribute("errore", "Dati personali non validi.");
                req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp").forward(req, resp);
                return;
            }

            // Crea il nuovo utente
            Utente utente = new Utente();
            utente.setNome(nome.trim());
            utente.setCognome(cognome.trim());
            utente.setEmail(email.trim().toLowerCase());
            utente.setPasswordHash(PasswordUtil.hash(password));
            utente.setRuolo(Utente.Ruolo.USER);

            utenteDAO.doSave(utente);

            // Login automatico dopo la registrazione
            HttpSession session = req.getSession();
            req.changeSessionId();
            session.setAttribute("utente", utente);
            CsrfToken.rotate(session);

            // Redirect: se stava comprando, torna al carrello
            String redirect = req.getParameter("redirect");
            if (redirect != null && redirect.equals("carrello")) {
                resp.sendRedirect(req.getContextPath() + "/carrello");
            } else {
                resp.sendRedirect(req.getContextPath() + "/home");
            }

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore durante la registrazione. Riprova.");
            req.getRequestDispatcher("/WEB-INF/view/registrazione.jsp")
               .forward(req, resp);
        }
    }
}