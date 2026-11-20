package cl.bcn.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Normaliza texto plano y contenido HTML antes de fragmentarlo.
 * Conserva saltos estructurales necesarios para las estrategias basadas en párrafos.
 */

public class LimpiadorTexto {

    public static String limpiar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        
        // Los saltos de etiqueta permiten conservar separadores útiles para chunking por párrafos.
        String textoLimpio = texto
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</(p|div|h[1-6]|li|tr|blockquote)>", "\n")
                .replaceAll("<[^>]*>", " ");
        
        // Las entidades decodificadas impiden que caracteres HTML alteren el significado del texto.
        Pattern decimalPattern = Pattern.compile("&#(\\d+);");
        Matcher decimalMatcher = decimalPattern.matcher(textoLimpio);
        StringBuffer sbDec = new StringBuffer();
        while (decimalMatcher.find()) {
            try {
                int charCode = Integer.parseInt(decimalMatcher.group(1));
                decimalMatcher.appendReplacement(sbDec, Character.toString((char) charCode));
            } catch (NumberFormatException e) {
                decimalMatcher.appendReplacement(sbDec, decimalMatcher.group(0));
            }
        }
        decimalMatcher.appendTail(sbDec);
        textoLimpio = sbDec.toString();

        // También se normalizan entidades hexadecimales y espacios antes de persistir.
        Pattern hexPattern = Pattern.compile("&#x([a-fA-F0-9]+);", Pattern.CASE_INSENSITIVE);
        Matcher hexMatcher = hexPattern.matcher(textoLimpio);
        StringBuffer sbHex = new StringBuffer();
        while (hexMatcher.find()) {
            try {
                int charCode = Integer.parseInt(hexMatcher.group(1), 16);
                hexMatcher.appendReplacement(sbHex, Character.toString((char) charCode));
            } catch (NumberFormatException e) {
                hexMatcher.appendReplacement(sbHex, hexMatcher.group(0));
            }
        }
        hexMatcher.appendTail(sbHex);
        
        return sbHex.toString()
                .replace("&nbsp;", " ")
                .replace("&aacute;", "á").replace("&eacute;", "é")
                .replace("&iacute;", "í").replace("&oacute;", "ó")
                .replace("&uacute;", "ú").replace("&ntilde;", "ñ")
                .replace("&Aacute;", "Á").replace("&Eacute;", "É")
                .replace("&Iacute;", "Í").replace("&Oacute;", "Ó")
                .replace("&Uacute;", "Ú").replace("&Ntilde;", "Ñ")
                .replace("&quot;", "\"").replace("&amp;", "&")
                .replace("&lt;", "<").replace("&gt;", ">")
                .replaceAll("[ \\t\\r\\f]+", " ")
                .replaceAll(" *\\n+ *", "\n")
                .trim();
    }
}
