package tests.mocks;

import mediator.Mediator;
import tests.factories.UserFactory;
import clientservice.IClientService;

import common.IUser;

public class ClientServiceMock implements IClientService {
	private Mediator med;
	private IUser me;
	
	public ClientServiceMock(Mediator med) {
		this.med = med;
		med.registerClientService(this);
	}

	@Override
	public void run() {
		/*try {
			Thread.sleep(600);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		UserFactory ufact = new UserFactory();
		
		me = ufact.produce("ME");
		med.userEnter(me);
		
		IUser dummyUser1 = ufact.produce();
		med.userEnter(dummyUser1);
		for (int i = 0; i < 10; i++) {
			med.userEnter(ufact.produce());
			med.userEnter(ufact.produce());
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	
		med.userExit(dummyUser1);
		
		
	}

	@Override
	public IUser getSelfUser() {
		return me;
	}

}
