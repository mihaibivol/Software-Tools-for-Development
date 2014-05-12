package app;


import gui.Gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import mediator.Mediator;
import network.Network;
import clientservice.ClientService;

public class Main {
	static Mediator med = new Mediator();
	
	public static void buildGUI() {
		JFrame frame = new JFrame("Transfers");
		frame.setContentPane(new Gui(med));
		
		frame.setSize(800, 600);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
	            med.exit();
	        }
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	public static void main(String[] args) throws IOException {
		// run on EDT (event-dispatching thread), not on main thread!
		final SwingWorker<?, ?> client = new ClientService(med, args[0], args[1]);
		final SwingWorker<?, ?> network = new Network(med);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGUI();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.execute();
				network.execute();
			}
		});
	}
}
