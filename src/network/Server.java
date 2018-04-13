package network;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * This class was created by the author below.
 * @author Almas Baimagambetov
 *
 */
public class Server extends NetworkConnection {

	private int port;
	
	public Server(int port, Consumer<Serializable> onReceiveCallback) {
		super(onReceiveCallback);
		this.port = port;
	}

	@Override
	protected boolean isServer() {
		return true;
	}

	@Override
	protected String getIP() {
		return null;
	}

	@Override
	protected int getPort() {
		return port;
	}

}
