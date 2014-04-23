package common;

import java.util.ArrayList;
import java.util.List;

public class LocalUser implements IUser {
	String name;
	String home;
	int listenPort;

	public LocalUser(String name, String home, int port) {
		this.name = name;
		this.home = home;
		this.listenPort = port;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IFile> getFiles() {
		return new ArrayList<IFile>();
	}
	public String getHome() {
		return home;
	}
	public String getDownloadLocation() {
		return "./" + home;
	}
	public int getPort() {
		return listenPort;
	}

	@Override
	public String toString() {
		return "ME";
	}
}
