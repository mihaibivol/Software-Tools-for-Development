package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Gui extends JPanel {
	
	private JList	files, users;
	private JTable transfers;
	final JProgressBar progressBar = new JProgressBar(0, 10);

	String[] columnNames = {"First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"};
	Object[][] data = {
		    {"Kathy", "Smith",
		     "Snowboarding", new Integer(5), new Boolean(false)},
		    {"John", "Doe",
		     "Rowing", new Integer(3), new Boolean(true)},
		    {"Sue", "Black",
		     "Knitting", new Integer(2), new Boolean(false)},
		    {"Jane", "White",
		     "Speed reading", new Integer(20), new Boolean(true)},
		    {"Joe", "Brown",
		     "Pool", new Integer(10), new Boolean(false)}
		};
	public Gui() {
		init();
	}
	
	public void init() {

		
		// TODO 1: populate model
		
		// initialize lists, based on the same model
		files	= new JList();
		users	= new JList();
		transfers = new JTable(data, columnNames);
		
		// TODO 6: redefine mirror so as to use a ReverseListModel instance on top of 'model'
		
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
		mainPanel.add(new JScrollPane(files), constraints);
		
		constraints.fill = GridBagConstraints.BOTH;
	    constraints.gridx = 0;
	    constraints.gridy = 1;
	    constraints.weighty = 200;
	    //constraints.ipady = 300;
		mainPanel.add(new JScrollPane(transfers), constraints);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(progressBar, BorderLayout.SOUTH);
		this.add(new JScrollPane(users), BorderLayout.EAST);
		

		
		

		/*
		this.add(top, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);
		
		// top panel: the two lists (scrollable)
		top.add(new JScrollPane(list));
		top.add(new JScrollPane(mirror));
		
		// bottom panel: name field, add button, remove button
		bottom.add(tName);
		bottom.add(bAdd);
		bottom.add(bRemove);
		
		bAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 2: call the method for obtaining the text field's content
				String text = "";
				
				if (text.isEmpty()) {
					JOptionPane.showMessageDialog(
							null, "Name is empty!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// TODO 3: add new element to model
			}
		});
		*/
		// TODO 4: add listener for Remove button
	}
	
	public static void buildGUI() {
		JFrame frame = new JFrame("Swing stuff"); // title
		frame.setContentPane(new Gui()); // content: the JPanel above
		frame.setSize(800, 600); // width / height
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit application when window is closed
		frame.setVisible(true); // show it!
	}


	public static void main(String[] args) {
		// run on EDT (event-dispatching thread), not on main thread!
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGUI();
			}
		});
	}

}
