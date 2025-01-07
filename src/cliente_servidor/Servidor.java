package cliente_servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
	private static List<String> canal1 = new ArrayList<String>();
	private static List<String> canal2 = new ArrayList<String>();
	private static List<String> canal3 = new ArrayList<String>();
	private static List<String> canal4 = new ArrayList<String>();
	private static List<List<String>> listaCanales = new ArrayList<List<String>>();

	private static List<Socket> canal1Sockets = new ArrayList<>();
	private static List<Socket> canal2Sockets = new ArrayList<>();
	private static List<Socket> canal3Sockets = new ArrayList<>();
	private static List<Socket> canal4Sockets = new ArrayList<>();
	private static List<List<Socket>> listaSockets = new ArrayList<>();

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		listaCanales.add(canal1);
		listaCanales.add(canal2);
		listaCanales.add(canal3);
		listaCanales.add(canal4);

		listaSockets.add(canal1Sockets);
		listaSockets.add(canal2Sockets);
		listaSockets.add(canal3Sockets);
		listaSockets.add(canal4Sockets);

		System.err.println("SERVIDOR >>> Arranca el servidor, espera peticion...");
		ServerSocket socketEscolta = null;
		try {
			socketEscolta = new ServerSocket(5000);
		} catch (IOException e) {
			System.err.println("SERVIDOR >>> Error");
			return;
		}
		while (true) {
			try {
				Socket connexio = socketEscolta.accept();
				System.err.println("SERVIDOR >>> Connexio rebuda --> Llança fil classe Peticio");

				HiloServidor servidor = new HiloServidor(connexio, canal1, canal2, canal3, canal4, listaCanales, listaSockets);
				Thread fil = new Thread(servidor);
				fil.start();
			} catch (IOException e) {
				System.err.println("SERVIDOR >>> Error al aceptar la conexión: " + e.getMessage());
			}
		}
	}

}
