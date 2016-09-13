import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable {

	private static final int PORT = 7;
	public ArrayList<String> usersList = new ArrayList<>();

	@Override
	public void run() {
		System.out.println("Uruchomiono TCP");
		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		while (true) {
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("I/O error: " + e);
			}
			
			System.out.println("Watek dla klienta");
			Thread c = new Thread(new HandleClientThread(clientSocket, this));
			c.start();
			
			if (!c.isAlive()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized boolean checkIfNickTaken(String nick) {
		return usersList.contains(nick);
	}
	
	public void addNickToList(String nick) {
		usersList.add(nick);
	}

}
