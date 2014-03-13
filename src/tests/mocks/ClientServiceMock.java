package tests.mocks;

import java.util.List;

import javax.swing.SwingWorker;

import mediator.Mediator;
import tests.factories.UserFactory;
import clientservice.IClientService;

import common.IUser;

public class ClientServiceMock extends SwingWorker<IUser, IUser> implements IClientService {
	private Mediator med;
	private IUser me;
	
	private IUser toRemove;
	
	public ClientServiceMock(Mediator med) {
		this.med = med;
		med.registerClientService(this);
	}
	
	@Override
	public IUser getSelfUser() {
		return me;
	}

	@Override
	protected IUser doInBackground() throws Exception {
		UserFactory ufact = new UserFactory();
		
		me = ufact.produce("ME");
		publish(me);
		
		IUser dummyUser1 = ufact.produce();
		publish(dummyUser1);
		for (int i = 0; i < 2; i++) {
			publish(ufact.produce());
			publish(ufact.produce());
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		toRemove = dummyUser1;
		publish(dummyUser1);
		
		for (int i = 0; i < 2; i++) {
			publish(ufact.produce());
			publish(ufact.produce());
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
	protected void process(List<IUser> chunks) {
		for (IUser u : chunks) {
			if (toRemove == u) {
				med.userExit(u);
				continue;
			}
			med.userEnter(u);
		}
	}

}
