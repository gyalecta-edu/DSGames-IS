package it.unisa.dsgames.control;

import javax.servlet.*;
import java.io.IOException;

/**
 * Filter che imposta l'encoding UTF-8 su tutte le richieste e risposte.
 */
public class EncodingFilter implements Filter {
    private static final long serialVersionUID = 1L;
    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String enc = filterConfig.getInitParameter("encoding");
        if (enc != null && !enc.isEmpty()) {
            this.encoding = enc;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // nulla da fare
    }
}