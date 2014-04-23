package network;

import java.net.InetSocketAddress;
import java.util.List;

import common.IFile;
import common.IUser;

public class NetworkUser implements IUser {
	String name;
	private String address = "localhost";
	private int listenPort = 1337;
	List<IFile> files;

	public NetworkUser(String name, List<IFile> files, int port) {
		this.name = name;
		this.files = files;
		this.listenPort = port;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IFile> getFiles() {
		return files;
	}
	
	public InetSocketAddress getAddress() {
		return new InetSocketAddress(address, listenPort);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
