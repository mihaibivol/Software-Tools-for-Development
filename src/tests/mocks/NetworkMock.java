package tests.mocks;

import java.util.ArrayList;

import tests.factories.FileFactory;
import tests.factories.UserFactory;
import common.IFile;
import common.IUser;

import mediator.Mediator;
import network.INetwork;

public class NetworkMock implements INetwork {
	private class Transfer {
		IUser src;
		IUser dst;
		IFile file;
		
		public Transfer(IUser src, IUser dst, IFile file) {
			this.src = src;
			this.dst = dst;
			this.file = file;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof Transfer) {
				Transfer o = (Transfer)other;
				return src.getName().equals(o.src.getName()) &&
					   dst.getName().equals(o.dst.getName()) &&
					   file.getName().equals(o.file.getName());
			} else {
				return false;
			}
		}
	}
	
	private Mediator med;
	ArrayList<Transfer> transfers;
	
	public NetworkMock(Mediator med) {
		this.med = med;
		transfers = new ArrayList<Transfer>();
		med.registerNetwork(this);
	}

	@Override
	public void downloadFile(IFile file, IUser user) {
		Transfer t = new Transfer(user, med.getSelfUser(), file);
		
		if (transfers.contains(t)) return;
		
		transfers.add(t);
		med.addDownload(user, med.getSelfUser(), file);
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);
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
