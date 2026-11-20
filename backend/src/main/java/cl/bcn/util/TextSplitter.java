package cl.bcn.util;

import java.util.ArrayList;
import java.util.List;
/**
 * Fragmenta texto por palabras, párrafos o estrategia híbrida adaptable.
 * Conserva el overlap configurado para mantener continuidad semántica entre fragmentos consecutivos.
 */

public class TextSplitter {
	
	/**
     * Divide el texto basándose en CANTIDAD DE PALABRAS.
     * @param texto El contenido completo.
     * @param palabrasPorChunk Cantidad máxima de palabras (ej: 500).
     * @param palabrasOverlap Cantidad de palabras que se repiten (ej: 50).
     */
	
	public static List<String> splitPorPalabras(String texto, int palabrasPorChunk, int palabrasOverlap) {
        List<String> chunks = new ArrayList<>();
        
        if (texto == null || texto.isEmpty()) return chunks;
        String[] palabras = texto.trim().split("\\s+");
        
        int totalPalabras = palabras.length;
        int paso = palabrasPorChunk - palabrasOverlap;
        if (paso <= 0) {
            paso = palabrasPorChunk; 
        }
        for (int i = 0; i < totalPalabras; i += paso) {
            int fin = Math.min(i + palabrasPorChunk, totalPalabras);
            StringBuilder chunkBuilder = new StringBuilder();
            for (int j = i; j < fin; j++) {
                chunkBuilder.append(palabras[j]).append(" ");
            }
            chunks.add(chunkBuilder.toString().trim());
            if (fin == totalPalabras) break;
        }

        return chunks;
    }
	
	public static List<String> splitPorParrafo(String texto) {
        List<String> chunks = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return chunks;
        }
        String[] parrafos = texto.split("\\r?\\n+");

        for (String parrafo : parrafos) {
            String parrafoLimpio = parrafo.trim();
            if (!parrafoLimpio.isEmpty()) {
                chunks.add(parrafoLimpio);
            }
        }
        return chunks;
    }
	
	public static List<String> splitEstructuralAvanzado(String texto, int tamanoChunk, int overlap) {
        List<String> chunksFinales = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return chunksFinales;
        }
        int paso = (tamanoChunk - overlap > 0) ? (tamanoChunk - overlap) : tamanoChunk;
        String[] parrafos = texto.split("\\r?\\n+");

        StringBuilder acumulador = new StringBuilder();
        int palabrasAcumuladas = 0;

        for (String parrafo : parrafos) {
            String parrafoLimpio = parrafo.trim();
            if (parrafoLimpio.isEmpty()) {
                continue;
            }

            String[] palabrasDelParrafo = parrafoLimpio.split("\\s+");
            int largoParrafo = palabrasDelParrafo.length;
            if (largoParrafo <= tamanoChunk) {
                if (palabrasAcumuladas + largoParrafo <= tamanoChunk) {
                    if (acumulador.length() > 0) {
                        acumulador.append("\n");
                    }
                    acumulador.append(parrafoLimpio);
                    palabrasAcumuladas += largoParrafo;
                } else {
                    chunksFinales.add(acumulador.toString());
                    acumulador.setLength(0);
                    acumulador.append(parrafoLimpio);
                    palabrasAcumuladas = largoParrafo;
                }
            } 
            else {
                if (acumulador.length() > 0) {
                    chunksFinales.add(acumulador.toString());
                    acumulador.setLength(0);
                    palabrasAcumuladas = 0;
                }

                int i = 0;
                while (i < largoParrafo) {
                    int fin = Math.min(i + tamanoChunk, largoParrafo);
                    StringBuilder chunkBuilder = new StringBuilder();

                    for (int j = i; j < fin; j++) {
                        chunkBuilder.append(palabrasDelParrafo[j]).append(" ");
                    }
                    chunksFinales.add(chunkBuilder.toString().trim());

                    if (fin >= largoParrafo) {
                        break;
                    }
                    i += paso;
                }
            }
        }
        if (acumulador.length() > 0) {
            chunksFinales.add(acumulador.toString());
        }

        return chunksFinales;
    }
}
