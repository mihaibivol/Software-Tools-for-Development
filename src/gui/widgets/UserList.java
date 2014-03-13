package gui.widgets;

import gui.IGui;

import javax.swing.JList;

import common.IUser;

import mediator.Mediator;

public class UserList extends JList<IUser> implements WidgetCommand {

	private static final long serialVersionUID = 1L;
	
	IGui gui;
	Mediator med;
	
	public UserList(Mediator med, IGui gui) {
		this.med = med;
		this.gui = gui;
		addListSelectionListener(gui);
	}

	@Override
	public void execute() {
		//TODO show user files from gui
	}

}
