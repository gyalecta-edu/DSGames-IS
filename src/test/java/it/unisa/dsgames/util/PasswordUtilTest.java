package it.unisa.dsgames.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void newHashUsesSaltAndValidatesPassword() {
        String first = PasswordUtil.hash("P4ssw0rd!Minimo");
        String second = PasswordUtil.hash("P4ssw0rd!Minimo");
        assertTrue(first.startsWith("pbkdf2_sha256$"));
        assertNotEquals(first, second, "Ogni hash deve avere un salt diverso");
        assertTrue(PasswordUtil.verify("P4ssw0rd!Minimo", first));
        assertFalse(PasswordUtil.verify("Errata", first));
    }

    @Test void supportsOldSha256Passwords() {
        assertTrue(PasswordUtil.verify("admin123",
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"));
        assertFalse(PasswordUtil.verify("errata",
            "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"));
        assertFalse(PasswordUtil.verify("test1234", "hash-corrotto"));
    }
}
