package mediator;

import java.util.List;

import common.IFile;
import common.IUser;
import common.LocalUser;
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
	
	public void registerNetwork(INetwork network) {
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
		//TODO remove user downloads from gui
	}
	
	public void refreshUsers(List<IUser> users) {
		gui.refreshUsers(users);;
	}
	
	/* Gui from Network specific actions */
	public void addDownload(IUser src, IUser dest, IFile file) {
		gui.addDownload(src, dest, file);
	}
	
	public void setDownloadProgress(IUser src, IUser dest, IFile file, int progress) {
		System.out.println("Set progress: " + progress);
		gui.setDownloadProgress(src, dest, file, progress);
		
		if (progress >= 100) {
			clientService.updateSelfFiles();
		}
	}
	
	/* Network from gui specific actions */
	public void downloadFile(IFile file, IUser owner) {
		network.downloadFile(file, owner);
	}
	
	/* ClientService specific actions */
	public LocalUser getSelfUser() {
		return clientService.getSelfUser();
	}
	
	public void exit() {
		clientService.exit();
	}
}
