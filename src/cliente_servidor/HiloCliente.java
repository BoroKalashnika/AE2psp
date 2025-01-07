package cliente_servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class HiloCliente implements Runnable {

	private final Socket socket;
	private String nombreUsu;
	private int canal;
	private boolean isEnvio;
	private boolean running;
	private boolean nomExiste;

	public HiloCliente(Socket socket, boolean nomExiste) {
		this.socket = socket;
		this.isEnvio = false;
		this.running = true;
		this.nomExiste = nomExiste;
	}

	public HiloCliente(Socket socket, String nombreUsu, int canal, boolean nomExiste) {
		this.socket = socket;
		this.nombreUsu = nombreUsu;
		this.canal = canal;
		this.isEnvio = true;
		this.running = true;
		this.nomExiste = nomExiste;
	}

	/**
	 * Al lanzar los hilos del Cliente y envio es tru podemos recibir y enviar
	 * mensajes al servidor, en caso contrario, salta una excepción por la
	 * comunicación.
	 */
	@Override
	public void run() {
		try {
			socket.setKeepAlive(true);

			if (isEnvio) {
				enviarMensajes();
			} else {
				recibirMensajes();
			}
		} catch (IOException e) {
			System.err.println("Error en la comunicación: " + e.getMessage());
		}
	}

	/**
	 * Envia mensajes al servidor con la información del usuario y su canal, tiene
	 * en cuenta las acciones que se toman en la interfaz gráfica.
	 */
	private void enviarMensajes() {
		@SuppressWarnings("resource")
		Scanner entrada = new Scanner(System.in);
		try (PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {
			pw.println(canal);

			pw.println(nombreUsu);

			System.out.println("Presiona ENTER para escribir");

			while (running) {
				if (!nomExiste) {
					entrada.nextLine();
					String mensaje = JOptionPane.showInputDialog(null, "Introduce 'exit' para cerrar", null,
							JOptionPane.INFORMATION_MESSAGE);
					if (mensaje.equalsIgnoreCase("exit")) {
						pw.println(mensaje);
						detener();
						break;
					} else {
						System.out.println(
								new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()) + ": " + mensaje);
						pw.println(mensaje);
					}
				} else {
					while (nomExiste) {
						nombreUsu = entrada.nextLine().replace(" ", "");
						pw.println(nombreUsu);
					}
				}
			}
			pw.flush();

		} catch (IOException e) {
			System.err.println("Error al enviar datos: " + e.getMessage());
		}
	}

	/**
	 * Recibe mensajes del servidor.
	 */
	private void recibirMensajes() {
		try (BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String mensaje;
			while (running && (mensaje = bfr.readLine()) != null) {
				if (mensaje.equals("El usuario ya existe, indica otro: ")) {
					nomExiste = true;
				} else {
					nomExiste = false;
				}
				System.err.println(mensaje);
			}
		} catch (IOException e) {
			System.err.println("Error al recibir mensajes: " + e.getMessage());
		}
	}

	/**
	 * Detiene la ejecución del servidor.
	 */
	private void detener() {
		try {
			running = false;
			socket.close();
		} catch (IOException e) {
			System.err.println("Error al cerrar el socket: " + e.getMessage());
		}
	}
}