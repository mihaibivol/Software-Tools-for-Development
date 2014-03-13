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
	private Mediator med;
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
	
	public Gui() {
		init();
		
		IFile dummyFile1 = new IFile() {
			
			@Override
			public String getName() {
				return "File1";
			}
		};
		
		IFile dummyFile2 = new IFile() {
			
			@Override
			public String getName() {
				return "File2";
			}
		};
		
		final ArrayList<IFile> dummyFiles = new ArrayList<IFile>();
		dummyFiles.add(dummyFile1);
		dummyFiles.add(dummyFile2);
		
		IUser dummyUser1 = new IUser() {
			
			@Override
			public String getName() {
				return "Gigi";
			}
			
			@Override
			public List<IFile> getFiles() {
				return dummyFiles;
			}
			
			public String toString() {
				return getName();
			}
		};
		
		IUser dummyUser2 = new IUser() {
			
			@Override
			public String getName() {
				return "Ionel";
			}
			
			@Override
			public List<IFile> getFiles() {
				return dummyFiles;
			}
			
			public String toString() {
				return getName();
			}
		};
		
		users.addElement(dummyUser1);
		users.addElement(dummyUser2);
		addDownload(dummyUser1, dummyUser2, dummyFile2);
	}
	
	public void init() {
		fileList	= new FileList(med, this);
		userList	= new UserList(med, this);
		transferList = new JTable(new DefaultTableModel(null, columnNames));
		
		transferList.getColumnModel().getColumn(3).setCellRenderer(new ProgressCellRenderer());
		
		files = (DefaultListModel<IFile>) fileList.getModel();
		users = (DefaultListModel<IUser>) userList.getModel();
		transfers = (DefaultTableModel) transferList.getModel();
		
		// main panel: top panel, bottom panel
		JPanel mainPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		
		GridBagConstraints constraints = new GridBagConstraints();	
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill   = GridBagConstraints.BOTH;
		constraints.weightx = 500;
		constraints.weighty = 400;
		gbl.setConstraints(mainPanel, constraints);
		mainPanel.setLayout(gbl);
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		mainPanel.add(new JScrollPane(fileList), constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    constraints.weighty = 200;
	    //constraints.ipady = 300;
		mainPanel.add(new JScrollPane(transferList), constraints);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(new JScrollPane(userList), BorderLayout.EAST);
	}
	
	public static void buildGUI() {
		JFrame frame = new JFrame("Transfers");
		frame.setContentPane(new Gui());
		
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	public static void main(String[] args) {
		// run on EDT (event-dispatching thread), not on main thread!
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGUI();
			}
		});
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
				"3%",
				"Started"
				});
	}

	@Override
	public void setDownloadProgress(IUser src, IUser dest, IFile file,
			int progress) {
		String prog = String.format("%s%%", progress);
		// TODO Auto-generated method stub
	}
	
	
	
	public void showFiles() {
		IUser user = userList.getSelectedValue();
		
		files.clear();
		for (IFile f : user.getFiles())
			files.addElement(f);
	}

}
