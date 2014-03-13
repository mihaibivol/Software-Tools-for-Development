package gui.widgets;

import gui.Gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import common.IUser;

import mediator.Mediator;

public class UserList extends JList<IUser> implements WidgetCommand {

	private static final long serialVersionUID = 1L;
	
	Gui gui;
	Mediator med;
	
	public UserList(Mediator med, Gui gui) {
		super(new DefaultListModel<IUser>());
		this.med = med;
		this.gui = gui;
		addListSelectionListener(gui);
	}

	@Override
	public void execute() {
		gui.showFiles();
	}

}
