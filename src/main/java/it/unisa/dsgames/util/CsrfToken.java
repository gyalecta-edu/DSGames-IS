package it.unisa.dsgames.util;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Token anti-CSRF imprevedibile e legato alla sessione, non all'email. */
public final class CsrfToken {
    private static final SecureRandom RANDOM = new SecureRandom();
    private CsrfToken() { }

    public static String ensure(HttpSession session) {
        synchronized (session) {
            String token = (String) session.getAttribute("token");
            if (token == null) return rotate(session);
            return token;
        }
    }

    public static String rotate(HttpSession session) {
        synchronized (session) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute("token", token);
            return token;
        }
    }

    public static boolean valid(HttpSession session, String received) {
        if (session == null || received == null) return false;
        Object original = session.getAttribute("token");
        return original instanceof String && MessageDigest.isEqual(
                ((String) original).getBytes(StandardCharsets.UTF_8),
                received.getBytes(StandardCharsets.UTF_8));
    }
}
