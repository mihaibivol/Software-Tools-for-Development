package app;

import gui.Gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import mediator.Mediator;
import tests.mocks.ClientServiceMock;
import tests.mocks.NetworkMock;

public class Main {
	static Mediator med = new Mediator();
	
	public static void buildGUI() {
		JFrame frame = new JFrame("Transfers");
		frame.setContentPane(new Gui(med));
		
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}


	public static void main(String[] args) {
		// run on EDT (event-dispatching thread), not on main thread!
		final SwingWorker<?, ?> client = new ClientServiceMock(med);
		final SwingWorker<?, ?> network = new NetworkMock(med);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGUI();
				client.execute();
				network.execute();
			}
		});
	}
}
