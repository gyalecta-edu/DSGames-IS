package it.unisa.dsgames.control;

import it.unisa.dsgames.dao.DAOFactory;
import it.unisa.dsgames.dao.UtenteDAO;
import it.unisa.dsgames.model.Utente;
import it.unisa.dsgames.util.PasswordUtil;
import it.unisa.dsgames.util.CsrfToken;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Se già loggato, redirect alla home
        if (req.getSession(false) != null &&
            req.getSession(false).getAttribute("utente") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/view/login.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            req.setAttribute("errore", "Inserisci email e password.");
            req.getRequestDispatcher("/WEB-INF/view/login.jsp")
               .forward(req, resp);
            return;
        }

        try {
            UtenteDAO utenteDAO = DAOFactory.getUtenteDAO();
            Utente utente = utenteDAO.doRetrieveByEmail(email.trim().toLowerCase());

            if (utente == null || !PasswordUtil.verify(password, utente.getPasswordHash())) {
                req.setAttribute("errore", "Email o password non corretti.");
                req.getRequestDispatcher("/WEB-INF/view/login.jsp")
                   .forward(req, resp);
                return;
            }

            // Login riuscito: salva utente e token in sessione
            HttpSession session = req.getSession();
            req.changeSessionId(); // mitiga session fixation preservando il carrello
            session.setAttribute("utente", utente);
            CsrfToken.rotate(session);

            // Redirect in base al ruolo
            if (utente.isAdmin()) {
                resp.sendRedirect(req.getContextPath() + "/admin/prodotti");
            } else {
                String redirect = req.getParameter("redirect");
                if (redirect != null && java.util.Arrays.asList("checkout", "carrello", "ordini", "profilo", "wishlist").contains(redirect)) {
                    resp.sendRedirect(req.getContextPath() + "/" + redirect);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/home");
                }
            }

        } catch (SQLException | NamingException e) {
            req.setAttribute("errore", "Errore durante il login. Riprova.");
            req.getRequestDispatcher("/WEB-INF/view/login.jsp")
               .forward(req, resp);
        }
    }
}