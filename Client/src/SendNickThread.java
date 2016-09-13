import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SendNickThread implements Runnable {

	private Socket socket;
	private ServerData server;
	private Random rand = new Random();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public SendNickThread(ServerData server) {
		super();
		this.server = server;
	}

	@Override
	public void run() {
		System.out.println("Start watku nick");

		try {
			socket = new Socket(server.getIp(), server.getPort());
			System.out.println("Polaczono z serwerem, ip " + server.getIp());

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			in.skip(socket.getInputStream().available());

			String nickResponse = "NICK ERROR";
			String sendData = "";
			
			do {
				sendData = getNickFromUser();
				out.write(sendData);
				out.flush();
				System.out.println("Wyslano " + sendData);

				nickResponse = in.readLine();
				System.out.println("Odpowiedz: " + nickResponse);
				
			} while (nickResponse.equals("NICK ERROR"));


			if (nickResponse.equals("NICK OK")) {
				
				try {
					int sendRate = getSendRateFromUser();
					System.out.println("utworzenie watku value");
					scheduler.scheduleAtFixedRate(new SendValueThread(out), 0, sendRate, TimeUnit.MILLISECONDS);
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			System.out.println("Nie mozna polaczyc z serwerem");
			Main.discoveryThread.start();
		} finally {
			System.out.println("Koniec watku nick");
			Main.onCloseNickThread(scheduler);
		}
	}

	public String getNickFromUser() {
		System.out.println("Wpisz swoj nick: ");
		Scanner s = new Scanner(System.in);
		String user = s.nextLine();
		return "NICK" + " " + user + "\n";
	}
	
	public int getSendRateFromUser() {
		int number;
		Scanner s = new Scanner(System.in);
		
		do {
			System.out.println("Podaj czesctotliwosc wysylania wartosci (10 - 10 000 ms): ");
			try {
			number = Integer.valueOf(s.nextLine());
			} catch(NumberFormatException e) {
				number = 0;
			}
			
		} while (number > 10000 || number < 10);
		
		return number;
	}

	public String sendRandomNumber() {
		return "VALUE ".concat(String.valueOf(rand.nextInt(10))).concat("\n");
	}
	
	
}
