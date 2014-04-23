package app;

import java.io.IOException;

import gui.Gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import network.Network;
import mediator.Mediator;
import tests.mocks.ClientServiceLocalFilesMock;
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


	public static void main(String[] args) throws IOException {
		// run on EDT (event-dispatching thread), not on main thread!
		final SwingWorker<?, ?> client = new ClientServiceLocalFilesMock(med, args[0]);
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
