import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HandleClientThread implements Runnable {

	private Socket clientSocket;
	private String clientNick;
	private ServerThread mainThread;

	public HandleClientThread(Socket clientSocket, ServerThread mainThread) {
		super();
		this.clientSocket = clientSocket;
		this.mainThread = mainThread;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		BufferedWriter out = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Polaczono klienta: " + clientSocket.getInetAddress());

		while (true) {
			try {
				clientNick = readNickFromUser(in);
				System.out.println("Otrzymano od klienta: " + clientNick);

				if (mainThread.checkIfNickTaken(clientNick) || clientNick.equals(null)) {
					out.write("NICK ERROR\n");
					out.flush();
				} else {
					out.write("NICK OK\n");
					out.flush();
					
					mainThread.addNickToList(clientNick);
					
					while (true) {
						String value = readValueFromUser(in);
						System.out.println(clientNick + ": " + value);
					}
				}

			} catch (IOException e) {
				System.out.println("Odlaczono klienta: " + clientNick);
				return;
			}
		}
	}

	private String readNickFromUser(BufferedReader reader) throws IOException {
		String response = reader.readLine();
		return response.substring(5);
	}

	private String readValueFromUser(BufferedReader reader) throws IOException {
		String response = reader.readLine();
		return response.substring(6);
	}

}
