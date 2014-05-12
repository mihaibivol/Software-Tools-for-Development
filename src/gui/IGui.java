package gui;

import java.util.EventListener;
import java.util.List;

import javax.swing.event.ListSelectionListener;

import common.IFile;
import common.IUser;

public interface IGui extends EventListener, ListSelectionListener {
	public void userEnter(IUser user);
	public void userExit(IUser user);
	
	public void addDownload(IUser src, IUser dest, IFile file);
	public void setDownloadProgress(IUser src, IUser dest, IFile file, int progress);
	
	public void refreshUsers(List<IUser> users);
}
