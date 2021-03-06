package gui;

import gui.widgets.FileList;
import gui.widgets.UserList;
import gui.widgets.WidgetCommand;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import common.IFile;
import common.IUser;
import mediator.Mediator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Gui extends JPanel implements IGui {
	static final long serialVersionUID = 1L;
	private FileList fileList;
	private UserList userList;
	private JTable transferList;
	
	private DefaultListModel<IUser> users;
	private DefaultListModel<IFile> files;
	private DefaultTableModel transfers;

	Object[] columnNames =
		   {"Source",
            "Destination",
            "File",
            "Progress",
            "Status"};
	
	public Gui(Mediator med) {
		med.registerGui(this);
	
		fileList	= new FileList(med, this);
		userList	= new UserList(med, this);
		transferList = new JTable(new DefaultTableModel(null, columnNames));
		
		transferList.getColumnModel().getColumn(3).setCellRenderer(new ProgressCellRenderer());
		
		files = (DefaultListModel<IFile>) fileList.getModel();
		users = (DefaultListModel<IUser>) userList.getModel();
		transfers = (DefaultTableModel) transferList.getModel();
		
		// main panel: top panel, bottom panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);

	    JScrollPane trasferListPane = new JScrollPane(transferList);
	    trasferListPane.setPreferredSize(new Dimension(120, 120));
		mainPanel.add(trasferListPane, BorderLayout.SOUTH);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		JScrollPane userListScrollPane = new JScrollPane(userList);
		userListScrollPane.setPreferredSize(new Dimension(200, 200));
		this.add(userListScrollPane, BorderLayout.EAST);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		((WidgetCommand)e.getSource()).execute();
	}

	@Override
	public void userEnter(IUser user) {
		users.addElement(user);
	}

	@Override
	public void userExit(IUser user) {
		users.removeElement(user);
	}

	@Override
	public void addDownload(IUser src, IUser dest, IFile file) {
		transfers.addRow(new Object[]
				{
				src,
				dest,
				file,
				"0%",
				"New"
				});
	}

	@Override
	public void setDownloadProgress(IUser src, IUser dest, IFile file,
			int progress) {
		String prog = String.format("%s%%", progress);
		
		for (int row = 0; row < transfers.getRowCount(); row++) {
			IUser rSrc = (IUser)transfers.getValueAt(row, 0);
			IUser rDest = (IUser)transfers.getValueAt(row, 1);
			IFile rFile = (IFile)transfers.getValueAt(row, 2);
			
			if (!rSrc.getName().equals(src.getName())) continue;
			if (!rDest.getName().equals(dest.getName())) continue;
			if (!rFile.getName().equals(file.getName())) continue;
			
			transfers.setValueAt(prog, row, 3);
			
			if (progress > 0) {
				transfers.setValueAt("Downloading", row, 4);
			}
			
			if (progress >= 100) {
				transfers.setValueAt("Complete", row, 4);
			}
		}
	}
	
	public void showFiles() {
		IUser user = userList.getSelectedValue();
		
		files.clear();
		if (user == null)
			return;
		for (IFile f : user.getFiles())
			files.addElement(f);
	}

	public IUser getSelectedUser() {
		return userList.getSelectedValue();
	}
	
	public IFile getSelectedFile() {
		return fileList.getSelectedValue();
	}

	@Override
	public void refreshUsers(List<IUser> users) {
		ArrayList<IUser> newUsers = new ArrayList<IUser>();
		ArrayList<IUser> removedUsers = new ArrayList<IUser>();
		for (IUser ru: users) {
			int i;
			for (i = 0; i < this.users.size(); i++) {
				IUser lu = this.users.get(i);
				
				if (lu.getName().equals(ru.getName())) {
					if (lu.getFiles().size() != ru.getFiles().size()) {
						this.users.set(i, ru);
					}
					break;
				}
			}
			if (i >= this.users.size()) {
				newUsers.add(ru);
			}
		}
		
		for (int i = 0; i < this.users.size(); i++) {
			boolean found = false;
			for (IUser ru: users) {
				if (ru.getName().equals(this.users.get(i).getName())) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				removedUsers.add(this.users.get(i));
			}
		}
		
		for (IUser u : newUsers) {
			this.users.addElement(u);
		}
		
		for (IUser u : removedUsers) {
			this.users.removeElement(u);
		}
	}

	
}
