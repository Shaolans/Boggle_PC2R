package boggle.client.tools;

public class ConnectInfo {
	private int port;
	private String server;
	private String username;
	
	public ConnectInfo(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getServer() {
		return server;
	}
	
	public String getUser() {
		return username;
	}
	
}
