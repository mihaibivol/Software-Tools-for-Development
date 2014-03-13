package app;

import gui.Gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import network.INetwork;

import tests.mocks.ClientServiceMock;
import tests.mocks.NetworkMock;

import clientservice.IClientService;

import mediator.Mediator;

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
		final IClientService client = new ClientServiceMock(med);
		final INetwork network = new NetworkMock(med);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildGUI();
				new Thread(client).start();
				new Thread(network).start();
			}
		});
	}
}
