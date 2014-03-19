package network;

import common.IFile;
import common.IUser;

public interface INetwork {
	public void downloadFile(IFile file, IUser user);
}
