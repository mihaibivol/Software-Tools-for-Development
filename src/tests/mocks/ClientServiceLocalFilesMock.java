package tests.mocks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import mediator.Mediator;
import network.NetworkUser;
import clientservice.IClientService;

import common.IFile;
import common.IUser;
import common.LocalUser;
import common.SimpleFile;

public class ClientServiceLocalFilesMock extends SwingWorker<IUser, IUser> implements IClientService {
	private Mediator med;
	private LocalUser me;
	String selfUserName;
	int port = 3001;
	private String homePrefix = "root/";
	String[] users = {"andrew", "florin", "ionut"};
	String[] files = {"file1.txt", "file2.pdf", "file3.avi"};
	
	
	public ClientServiceLocalFilesMock(Mediator med, String selfUserName) {
		this.med = med;
		med.registerClientService(this);
		this.selfUserName = selfUserName;
	}
	
	@Override
	public LocalUser getSelfUser() {
		return me;
	}

	@Override
	protected IUser doInBackground() throws Exception {
		for (String user : users) {
			
			String home = homePrefix + user + "/";
			System.out.println("Home: "+ home);
			File dir = new File(home);
			String[] fileList = dir.list();
			List<IFile> files = new ArrayList<IFile>();
			for (String file : fileList) {
				files.add(new SimpleFile(file));
				File f = new File(home + file);
				System.out.println("File: "+ home + file + " - " + f.exists());
			}
			
			
			if (user.equals(selfUserName)) {
				me = new LocalUser(user, home, port++);
				publish(me);
				continue;
			}

			IUser usr = new NetworkUser(user, files, port++);
			publish(usr);
		}
		return null;
	}
	
	@Override
	protected void process(List<IUser> chunks) {
		for (IUser user: chunks) {
			med.userEnter(user);
		}
	}

	@Override
	public void updateSelfFiles() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}
