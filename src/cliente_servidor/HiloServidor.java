package cliente_servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HiloServidor implements Runnable {
	private Socket socket;
	private String nomUsu;
	private int canal;
	private List<String> canal1;
	private List<String> canal2;
	private List<String> canal3;
	private List<String> canal4;
	private List<List<String>> listaCanales;
	private List<List<Socket>> listaSockets;

	public HiloServidor(Socket socket, List<String> canal1, List<String> canal2, List<String> canal3,
			List<String> canal4, List<List<String>> listaCanales, List<List<Socket>> listaSockets) {
		this.socket = socket;
		this.canal1 = canal1;
		this.canal2 = canal2;
		this.canal3 = canal3;
		this.canal4 = canal4;
		this.listaCanales = listaCanales;
		this.listaSockets = listaSockets;
	}

	/**
	 * El servidor recibe y envia mensajes a los clientes,controla que el usuario no
	 * exista repetidas veces en los mismos canales existentes, lee la información
	 * del cliente y la acción que envia el cliente. Entre las acciones podemos
	 * visualizar los canales, enviar mensajes a un canal específico, obtener el
	 * listado con los participantes de un canal, etc.
	 */
	@Override
	public void run() {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream outputStream = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(outputStream, true)) {

			System.err.println("SERVIDOR >>> Esperando la seleccion del canal");
			canal = Integer.parseInt(bufferedReader.readLine());

			nomUsu = bufferedReader.readLine();

			listaSockets.get(canal - 1).add(socket);

			boolean noExisteUsu = anyadirParticipanteCanal();
			while (!noExisteUsu) {
				pw.println("El usuario ya existe, indica otro: ");
				nomUsu = bufferedReader.readLine();
				noExisteUsu = anyadirParticipanteCanal();
			}

			System.err.println("SERVIDOR >>> Usuario " + nomUsu + " ha seleccionado el canal " + canal);

			String opcionMensaje;
			while ((opcionMensaje = bufferedReader.readLine().trim()) != null) {
				if (opcionMensaje.equals("whois")) {
					pw.println(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + ": "
							+ obtenerParticipantesCanal());
					System.err.println("SERVIDOR >>> " + nomUsu + " (canal" + canal + ") >>>" + opcionMensaje);
				} else if (opcionMensaje.equals("exit")) {
					quitarParticipanteCanal();
					return;
				} else if (opcionMensaje.equals("channels")) {
					pw.println(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + ": "
							+ ": Canales disponibles: " + Utiles.visualizarCanales(Utiles.obtenerListaCanales()));
				} else if (opcionMensaje.startsWith("@canal")) {
					enviarMensajePorCanal(opcionMensaje, Integer.parseInt(opcionMensaje.charAt(6) + ""));
				} else {
					enviarMensajePorCanal(opcionMensaje, canal);
				}
			}
		} catch (IOException e) {
			System.err.println("SERVIDOR >>> Error de I/O: " + e.getMessage());
		}
	}

	/**
	 * Envia un mensaje a un canal cogiendo la fecha de envío, el usuario y el
	 * mensaje que se envia.
	 * 
	 * @param opcionMensaje la accion al enviar el mensaje.
	 * @param canal         donde se quiere enviar el mensaje.
	 * @throws IOException control de excepciones.
	 */
	private void enviarMensajePorCanal(String opcionMensaje, int canal) throws IOException {
		String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
		for (Socket canalSocket : listaSockets.get(canal - 1)) {
			if (canalSocket != socket) {
				PrintWriter canalPw = new PrintWriter(canalSocket.getOutputStream(), true);
				canalPw.println(simpleDateFormat + ": " + nomUsu + " >>> " + opcionMensaje);
				System.err.println("SERVIDOR >>> " + nomUsu + " (canal" + canal + ") >>>" + opcionMensaje);
			}
		}
	}

	/**
	 * Obtiene un listado con todos los usuarios activos de cada canal.
	 * 
	 * @return un string con los usuarios activos de cada canal.
	 */
	private String obtenerParticipantesCanal() {
		StringBuilder participantes = new StringBuilder();
		for (int i = 0; i < listaCanales.size(); i++) {
			if (canal == i + 1) {
				for (String string : listaCanales.get(i)) {
					participantes.append(" " + string + " |");
				}
			}
		}
		return "Usuarios activos canal" + canal + ": " + participantes.toString();
	}

	/**
	 * Obtiene el canal actual y añade al usuario la lista con los usuarios de ese
	 * canal.
	 * 
	 * @return true si el usuario se ha añadido al canalActual, false si el usuario
	 *         ya existe en la lista.
	 */
	private boolean anyadirParticipanteCanal() {
		List<String> canalActual = listaCanales.get(canal - 1);
		if (canalActual.contains(nomUsu)) {
			return false;
		}
		canalActual.add(nomUsu);
		return true;
	}

	/**
	 * Toma el canal donde se encuentra el usuario y lo elimina de la lista de
	 * canales a la cual pertenece.
	 */
	private void quitarParticipanteCanal() {
		switch (canal) {
		case 1:
			canal1.removeIf(string -> string.equals(nomUsu));
			break;
		case 2:
			canal2.removeIf(string -> string.equals(nomUsu));
			break;
		case 3:
			canal3.removeIf(string -> string.equals(nomUsu));
			break;
		case 4:
			canal4.removeIf(string -> string.equals(nomUsu));
			break;
		}
	}

}
