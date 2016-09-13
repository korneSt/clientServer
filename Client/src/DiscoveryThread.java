import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

public class DiscoveryThread implements Runnable {

	private DatagramSocket c;
	private DatagramPacket receivePacket;
	private boolean foundServer = false;
	private static DiscoveryThread instance = null;

	protected DiscoveryThread() {
	}

	public synchronized static DiscoveryThread getInstance() {
		if (instance == null) {
			instance = new DiscoveryThread();
		}
		return instance;
	}

	@Override
	public void run() {
		foundServer = false;
		try {
			c = new DatagramSocket();
			c.setBroadcast(true);
			c.setSoTimeout(5000);
			while (!foundServer) {
				sendMessege();
				try {
					c.receive(receivePacket);
				} catch (SocketTimeoutException e) {
					continue;
				}

				System.out.println(
						"Odpowiedz od serwera: " + receivePacket.getAddress() + ", port " + receivePacket.getPort());

				String message = new String(receivePacket.getData()).trim();

				if (message.equals("OFFER")) {
					foundServer = true;
					Main.saveNewServer(receivePacket.getAddress().toString().substring(1), receivePacket.getPort());
				}

				c.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			Main.onCloseDiscoveryThread();
		}

	}

	public void sendMessege() {
		byte[] sendData = "DISCOVER".getBytes();
		try {

			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName("255.255.255.255"), 7);
				c.send(sendPacket);
				System.out.println("Wysylam zapytanie do 255.255.255.255");
			} catch (Exception e) {
				e.printStackTrace();
			}

			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue;
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
						c.send(sendPacket);
					} catch (Exception e) {
					}

					System.out.println("Wysylam zapytanie do: " + broadcast.getHostAddress() + "; interfejs: "
							+ networkInterface.getDisplayName());
				}
			}

			System.out.println("Czekam na odpowiedz serwera...");

			byte[] recvBuf = new byte[1000];
			receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
