import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscoveryThread implements Runnable {

	private static DiscoveryThread instance = null;
	private DatagramSocket socket;

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
		System.out.println("Uruchomiono UDP");
		try {
			socket = new DatagramSocket(7, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			while (true) {
				System.out.println("Nasluchuje pakietow Broadcast!");

				//odbierz pakiet
				byte[] recvBuf = new byte[100];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				System.out.println("Odebrano pakiet od: " + packet.getAddress().getHostAddress() + ", dane: " + packet.getData().toString());

				String message = new String(packet.getData()).trim();
				
				if (message.equals("DISCOVER")) {
					byte[] sendData = "OFFER".getBytes();

					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
							packet.getPort());
					socket.send(sendPacket);

					System.out.println("Wyslano odpowiedz do: " + sendPacket.getAddress().getHostAddress());

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
