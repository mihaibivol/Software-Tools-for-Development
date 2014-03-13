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
	
	public void registerClientService(IClientService clientService) {
		this.clientService = clientService;
	}
	
	/* Gui from ClinetService specific actions */
	public void userEnter(IUser user) {
		gui.userEnter(user);
	}
	
	public void userExit(IUser user) {
		gui.userExit(user);
	}
	
	/* Gui from Network specific actions */
	void addDownload(IUser src, IUser dest, IFile file) {
		gui.addDownload(src, dest, file);
	}
	
	void setDownloadProgress(IUser src, IUser dest, IFile file, int progress) {
		gui.setDownloadProgress(src, dest, file, progress);
	}
	
	/* Network from gui specific actions */
	void downloadFile(IFile file, IUser owner) {
		network.downloadFile(file, owner);
	}
	
	/* ClientService specific actions */
	public IUser getSelfUser() {
		return clientService.getSelfUser();
	}
}
