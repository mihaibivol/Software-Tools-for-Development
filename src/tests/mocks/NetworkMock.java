package tests.mocks;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import tests.factories.FileFactory;
import tests.factories.UserFactory;
import common.IFile;
import common.IUser;
import mediator.Mediator;
import network.INetwork;
import network.NetworkUser;

class Transfer extends SwingWorker<Integer, Integer>{
	IUser src;
	IUser dst;
	IFile file;
	Mediator med;
	
	public Transfer(IUser src, IUser dst, IFile file, Mediator med) {
		this.src = src;
		this.dst = dst;
		this.file = file;
		this.med = med;
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

	@Override
	protected Integer doInBackground() throws Exception {
		for (int progress = 0; progress <= 100; progress += 10) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			publish(progress);	
		}
		return null;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		for (Integer i : chunks)
			med.setDownloadProgress(src, dst, file, i);
	}
}

public class NetworkMock extends SwingWorker<Transfer, Transfer> implements INetwork {
	private Mediator med;
	ArrayList<Transfer> transfers;
	
	public NetworkMock(Mediator med) {
		this.med = med;
		transfers = new ArrayList<Transfer>();
		med.registerNetwork(this);
	}

	@Override
	public void downloadFile(IFile file, IUser user) {
		Transfer t = new Transfer(user, med.getSelfUser(), file, med);
		
		if (transfers.contains(t)) return;	
		transfers.add(t);
		
		publish(t);
	}

	@Override
	protected Transfer doInBackground() throws Exception {
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
		Transfer t = new Transfer(u1, u2, file, med);
		publish(t);
		return null;
	}

	@Override
	protected void process(List<Transfer> chunks) {
		for (Transfer t : chunks) {
			med.addDownload(t.src, t.dst, t.file);
			t.execute();
		}
	}
}
