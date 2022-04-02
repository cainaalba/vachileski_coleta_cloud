package pneus.vachileski_mobi_funcoes_genericas;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CriptografaString {
    private final Cipher cipher;
    private final SecretKeySpec key;
    private final AlgorithmParameterSpec spec;
    private static final String SEED_16_CHARACTER = "U1MjU1M0FDOUZ.Qz";

    public CriptografaString() throws Exception {
        // hash password with SHA-256 and crop the output to 128-bit for key
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(SEED_16_CHARACTER.getBytes(StandardCharsets.UTF_8)); //"UTF-8"
        byte[] keyBytes = new byte[32];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        key = new SecretKeySpec(keyBytes, "AES");
        spec = getIV();
    }

    private AlgorithmParameterSpec getIV() {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);

        return ivParameterSpec;
    }

    public String encrypt(String plainText) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        //String encryptedText = new String(Base64.encode(encrypted, Base64.DEFAULT), StandardCharsets.UTF_8);

        return new String(Base64.encode(encrypted, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public String decrypt(String cryptedText) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] bytes = Base64.decode(cryptedText, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(bytes);
        //String decryptedText = new String(decrypted, StandardCharsets.UTF_8);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
