import java.io.BufferedWriter;
import java.util.Random;

public class SendValueThread implements Runnable {

	private Random rand;
	private BufferedWriter out;

	public SendValueThread() {

	}

	public SendValueThread(BufferedWriter out) {
		this.out = out;
		rand = new Random();
	}

	@Override
	public void run() {
		try {
			System.out.println("Wysylam wartosc");

			out.write(sendRandomNumber());
			out.flush();
		} catch (Exception e) {
			Main.onCloseSendThread();
			System.out.println("Blad wyslania wartosc");
		}
	}

	public void setPrintWriter(BufferedWriter out) {
		this.out = out;
	}

	public String sendRandomNumber() {
		return "VALUE ".concat(String.valueOf(rand.nextInt(10))).concat("\n");
	}

}
