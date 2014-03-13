package tests.mocks;

import tests.factories.FileFactory;
import tests.factories.UserFactory;
import common.IFile;
import common.IUser;

import mediator.Mediator;
import network.INetwork;

public class NetworkMock implements INetwork {
	private Mediator med;
	
	public NetworkMock(Mediator med) {
		this.med = med;
		med.registerNetwork(this);
	}

	@Override
	public void downloadFile(IFile file, IUser user) {
		med.addDownload(user, med.getSelfUser(), file);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		UserFactory ufact = new UserFactory();
		FileFactory ffact = new FileFactory();
		IUser u1 = ufact.produce();
		IUser u2 = ufact.produce();
		IFile file = ffact.produce();
		
		int progress = 0;
		
		med.addDownload(u1, u2, file);
		for (progress = 0; progress <= 100; progress += 10) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			med.setDownloadProgress(u1, u2, file, progress);
		}
	}

}
