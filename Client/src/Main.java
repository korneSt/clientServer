import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ScheduledExecutorService;

public class Main {


	private static ArrayList<ServerData> serverDataList;
	private static ArrayList<ServerData> foundServersList;
	private static Thread sendNickThread;
	private static ServerData selectedServer;
	private static Scanner s;
	protected static Thread discoveryThread;

	public static void main(String[] args) {
		s = new Scanner(System.in);
		serverDataList = new ArrayList<>();
		foundServersList = new ArrayList<>();
		discoveryThread = new Thread (DiscoveryThread.getInstance());
		loadServerDataFromFile("serwery.ser");
		if (!serverDataList.isEmpty())
			connectToLastServer();
		else
			discoveryThread.start();

	}

	public static ServerData selectServerFromList() {
		System.out.println("Znalezione serwery");
		for (ServerData s : foundServersList) {
			System.out.println(s.getIp() + " " + s.getPort());
		}

		System.out.println("Wybierz serwer z ktorym polaczyc");
		
		int choice = -1;
		do {
			try {
				choice = Integer.valueOf(s.nextLine());
			} catch (NumberFormatException e) {
				choice = -1;
			}
		} while (choice < 0 || choice >= serverDataList.size());

		return foundServersList.get(choice);
	}

	public static void saveNewServer(String ip, int port) {
		foundServersList.add(new ServerData(ip, port));
		saveServerDataToFile("serwery.ser");
	}

	private static void saveServerDataToFile(String file) {
		System.out.println("Zapis serwerow do pliku");
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, false))) {
			oos.writeObject(foundServersList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadServerDataFromFile(String file) {
		System.out.println("Ladowanie serwerow z pliku");
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			serverDataList = (ArrayList<ServerData>) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Nie mozna znalezc okreslonego pliku");
		}
	}

	public static void onCloseDiscoveryThread() {
		selectedServer = selectServerFromList();
		sendNickThread = new Thread(new SendNickThread(selectedServer));
		sendNickThread.start();

		if (!sendNickThread.isAlive()) {
			System.out.println("KONIEC watku NICK");
		}

	}

	static ScheduledExecutorService sendValueScheduler1;

	public static void onCloseNickThread(ScheduledExecutorService sendValueScheduler) {
		sendValueScheduler1 = sendValueScheduler;
		while (true) {
			if (sendValueScheduler.isShutdown()) {
				System.out.println("Koniec watku VALUE");
				break;
			}
		}
		new Thread (DiscoveryThread.getInstance()).start();
	}

	private static void connectToLastServer() {
		System.out.println("Polacz z ostatnim serwerem t/n");
		String response = s.nextLine();
		if (response.equals("t")) {
			sendNickThread = new Thread(new SendNickThread(serverDataList.get(serverDataList.size() - 1)));
			sendNickThread.start();
		} else {
			new Thread(DiscoveryThread.getInstance()).start();
		}
	}

	public static void onCloseSendThread() {
		// TODO Auto-generated method stub
		sendValueScheduler1.shutdown();
	}
}
