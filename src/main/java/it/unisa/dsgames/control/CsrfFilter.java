package it.unisa.dsgames.control;

import it.unisa.dsgames.util.CsrfToken;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/** Verifica il token per tutte le richieste mutative; ammette letture GET senza effetti. */
public class CsrfFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        HttpSession session = req.getSession(false);
        if (!path.startsWith("/styles/") && !path.startsWith("/scripts/") &&
                !path.startsWith("/images/")) {
            session = req.getSession();
            CsrfToken.ensure(session);
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String supplied = req.getHeader("X-CSRF-Token");
            if (supplied == null) supplied = req.getParameter("token");
            if (!CsrfToken.valid(session, supplied)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Sessione scaduta o token non valido.");
                return;
            }
        }
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        chain.doFilter(request, response);
    }
}
