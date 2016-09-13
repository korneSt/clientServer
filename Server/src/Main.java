public class Main {

	public static void main(String[] args) {
		new Thread(DiscoveryThread.getInstance()).start();
		new Thread(new ServerThread()).start();
	}
}
