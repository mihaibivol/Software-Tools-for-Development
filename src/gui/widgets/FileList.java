package gui.widgets;

import gui.Gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import mediator.Mediator;

import common.IFile;

public class FileList extends JList<IFile> implements WidgetCommand {
	private static final long serialVersionUID = 1L;
	Gui gui;
	Mediator med;
	
	public FileList(Mediator med, Gui gui) {
		super(new DefaultListModel<IFile>());
		this.med = med;
		this.gui = gui;
		addListSelectionListener(gui);
	}

	@Override
	public void execute() {
		//TODO start transfer from mediator
	}
}
