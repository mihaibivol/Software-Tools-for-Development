package tests.mocks;

import mediator.Mediator;
import tests.factories.UserFactory;
import clientservice.IClientService;

import common.IUser;

public class ClientServiceMock implements IClientService {
	private Mediator med;
	
	public ClientServiceMock(Mediator med) {
		this.med = med;
		med.registerClientService(this);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		UserFactory ufact = new UserFactory();
		
		IUser dummyUser1 = ufact.produce();
		IUser dummyUser2 = ufact.produce();
		IUser dummyUser3 = ufact.produce();
		
		med.userEnter(dummyUser1);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		med.userEnter(dummyUser2);
		med.userEnter(dummyUser3);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		med.userExit(dummyUser1);
		
		
	}

}
