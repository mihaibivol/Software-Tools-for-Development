package mediator;

import common.IFile;
import common.IUser;

import clientservice.IClientService;
import network.INetwork;
import gui.IGui;

public class Mediator {
	IGui gui;
	INetwork network;
	IClientService clientService;
	
	public void registerGui(IGui gui) {
		this.gui = gui;
	}
	
	void registerNetwork(INetwork network) {
		this.network = network;
	}
	
	void registerClientService(IClientService clientService) {
		this.clientService = clientService;
	}
	
	/* Gui specific actions */
	void userEnter(IUser user) {
		gui.userEnter(user);
	}
	
	void userExit(IUser user) {
		gui.userExit(user);
	}
	
	void addDownload(IUser src, IUser dest, IFile file) {
		gui.addDownload(src, dest, file);
	}
	
	void setDownloadProgress(IUser src, IUser dest, IFile file, int progress) {
		gui.setDownloadProgress(src, dest, file, progress);
	}
	
	/* Network specific actions */
	void downloadFile(IFile file, IUser owner) {
		network.downloadFile(file, owner);
	}
}
