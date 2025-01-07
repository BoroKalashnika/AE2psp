package cliente_servidor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Cliente {
	private static boolean nomExiste = false;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		Scanner entrada = new Scanner(System.in);
		System.out.print("IP: ");
		String ip = entrada.nextLine();
		System.out.print("Puerto: ");
		int puerto = entrada.nextInt();
		InetSocketAddress direccio = new InetSocketAddress(ip, puerto);

		Socket socket = new Socket();
		socket.connect(direccio);

		String formatData = "yyyy-MM-dd_HH-mm-ss";
		String simpleDateFormat = new SimpleDateFormat(formatData).format(new Date());

		System.out.println(
				simpleDateFormat + ": Canales disponibles: " + Utiles.visualizarCanales(Utiles.obtenerListaCanales()));

		System.out.print("Selecciona canal: ");
		int canal = entrada.nextInt();
		entrada.nextLine();
		System.out.print("Indica nombre de usuario: ");
		String nombreUsu = entrada.nextLine();

		Thread enviarThread = new Thread(new HiloCliente(socket, nombreUsu.replace(" ", ""), canal, nomExiste));
		enviarThread.start();

		Thread recibirThread = new Thread(new HiloCliente(socket, nomExiste));
		recibirThread.start();

	}

}
