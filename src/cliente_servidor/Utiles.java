package cliente_servidor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utiles {

	/***
	 * Lee el fichero canals.txt para obtener una lista con los canales disponibles
	 * y añade los canales dentro de una lista de strings para obtener los canales.
	 * 
	 * @return una lista con los canales disponibles.
	 */
	public static List<String> obtenerListaCanales() {
		List<String> canales = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("./src/canals.txt"));
			String linea = br.readLine();

			while (linea != null) {
				if (linea != null) {
					canales.add(linea);
					linea = br.readLine();
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canales;
	}

	/***
	 * Coge los canales disponibles de la lista de canales y los visualiza en un
	 * string, el formato es: [ elem1, elem2, elem3, elem4 ]
	 * 
	 * @param canales disponibles
	 * @return un string con el formato de los canales disponibles.
	 */
	public static String visualizarCanales(List<String> canales) {
		StringBuilder canalesString = new StringBuilder("[");
		for (int i = 0; i < canales.size(); i++) {
			if (i == canales.size() - 1) {
				canalesString.append(canales.get(i)).append("]");
			} else {
				canalesString.append(canales.get(i)).append(", ");
			}
		}
		return canalesString.toString();
	}
	
}