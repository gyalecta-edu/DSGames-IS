package it.unisa.dsgames.control;

import it.unisa.dsgames.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filtro generale per il controllo degli accessi.
 * Protegge le URL che richiedono autenticazione utente.
 * Le URL admin sono gestite separatamente da AdminFilter.
 */
@WebFilter("/*")
public class AccessControlFilter implements Filter {

    // URL che richiedono login (esatte)
    private static final Set<String> PROTECTED_PATHS = new HashSet<>(Arrays.asList(
        "/checkout",
        "/ordini",
        "/profilo",
        "/wishlist"
    ));

    // URL pubbliche (non richiedono login)
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
        "/home",
        "/catalogo",
        "/prodotto",
        "/offerte",
        "/login",
        "/registrazione",
        "/carrello",
        "/logout"
    ));

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String requestURI  = req.getRequestURI();
        String path = requestURI.substring(contextPath.length());

        // Lascia passare risorse statiche (CSS, JS, immagini)
        if (path.startsWith("/styles/")  ||
            path.startsWith("/scripts/") ||
            path.startsWith("/images/")  ||
            path.equals("/")) {
            chain.doFilter(request, response);
            return;
        }

        // Rimuove parametri dal path per il check
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // Controlla se la path è protetta
        if (PROTECTED_PATHS.contains(path)) {
            HttpSession session = req.getSession(false);
            Utente utente = (session != null)
                ? (Utente) session.getAttribute("utente") : null;

            if (utente == null) {
                // Salva la destinazione originale per il redirect post-login
                String destination = path.substring(1); // rimuove lo slash iniziale
                resp.sendRedirect(contextPath +
                    "/login?redirect=" + destination);
                return;
            }
        }

        // Impedisce agli utenti loggati di andare su login/registrazione
        if (path.equals("/login") || path.equals("/registrazione")) {
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("utente") != null) {
                resp.sendRedirect(contextPath + "/home");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}