package gui.widgets;

import javax.swing.JList;

import common.IFile;

import gui.IGui;
import mediator.Mediator;

public class FileList extends JList<IFile> implements WidgetCommand {
	private static final long serialVersionUID = 1L;
	IGui gui;
	Mediator med;
	
	public FileList(Mediator med, IGui gui) {
		this.med = med;
		this.gui = gui;
		addListSelectionListener(gui);
	}

	@Override
	public void execute() {
		//TODO start transfer from mediator
	}
}
