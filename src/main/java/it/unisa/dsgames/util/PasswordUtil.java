package it.unisa.dsgames.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** PBKDF2-SHA256 per nuovi account; verifica SHA-256 legacy per la migrazione. */
public final class PasswordUtil {
    private static final int ITERATIONS = 210000;
    private static final int LENGTH = 32;
    private static final SecureRandom RNG = new SecureRandom();
    private PasswordUtil() { }

    public static String hash(String password) {
        if (password == null) throw new IllegalArgumentException("Password mancante");
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] encoded = derive(password, salt, ITERATIONS);
        return "pbkdf2_sha256$" + ITERATIONS + "$" +
                Base64.getEncoder().encodeToString(salt) + "$" +
                Base64.getEncoder().encodeToString(encoded);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;
        try {
            if (stored.startsWith("pbkdf2_sha256$")) {
                String[] parts = stored.split("\\$", -1);
                if (parts.length != 4) return false;
                int iterations = Integer.parseInt(parts[1]);
                if (iterations < 100000 || iterations > 1000000) return false;
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expected = Base64.getDecoder().decode(parts[3]);
                if (salt.length != 16 || expected.length != LENGTH) return false;
                return MessageDigest.isEqual(expected, derive(password, salt, iterations));
            }
            // Compatibilità con gli account preesistenti (SHA-256 hex)
            if (!stored.matches("[0-9a-fA-F]{64}")) return false;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] candidate = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(candidate, java.util.HexFormat.of().parseHex(stored));
        } catch (IllegalArgumentException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, LENGTH * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 non disponibile", e);
        } finally {
            spec.clearPassword();
        }
    }
}
