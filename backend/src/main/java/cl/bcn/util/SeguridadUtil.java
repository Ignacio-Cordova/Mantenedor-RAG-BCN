package cl.bcn.util;


import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.ByteBuffer;
/**
 * Cifra y descifra datos sensibles con AES/GCM.
 * El vector de inicialización se genera por operación y se almacena junto al texto cifrado en Base64.
 */

public class SeguridadUtil {

    private static final String LLAVE_MAESTRA = "BcnSecretKey2026"; 
    private static final String ALGORITMO = "AES";
    private static final String MODO_OPERACION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; 
    private static final int IV_LENGTH = 12; 

    public static String encriptar(String textoPlano) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        SecretKeySpec secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes("UTF-8"), ALGORITMO);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(MODO_OPERACION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
        byte[] textoEncriptado = cipher.doFinal(textoPlano.getBytes("UTF-8"));
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + textoEncriptado.length);
        byteBuffer.put(iv);
        byteBuffer.put(textoEncriptado);
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    public static String desencriptar(String textoBase64) throws Exception {
        byte[] bytesEmpaquetados = Base64.getDecoder().decode(textoBase64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytesEmpaquetados);
        byte[] iv = new byte[IV_LENGTH];
        byteBuffer.get(iv);
        byte[] textoEncriptado = new byte[byteBuffer.remaining()];
        byteBuffer.get(textoEncriptado);
        SecretKeySpec secretKey = new SecretKeySpec(LLAVE_MAESTRA.getBytes("UTF-8"), ALGORITMO);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        Cipher cipher = Cipher.getInstance(MODO_OPERACION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        byte[] textoDesencriptado = cipher.doFinal(textoEncriptado);
        return new String(textoDesencriptado, "UTF-8");
    }
}
