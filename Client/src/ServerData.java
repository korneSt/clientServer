import java.io.Serializable;

public class ServerData implements Serializable{
	
	private String ip;
	private int port;
	
	public ServerData(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public ServerData() {
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
}
