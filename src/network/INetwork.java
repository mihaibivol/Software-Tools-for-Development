package network;

import common.IFile;
import common.IUser;

public interface INetwork extends Runnable {
	public void downloadFile(IFile file, IUser user);
}
