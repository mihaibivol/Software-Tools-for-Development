package clientservice;

import common.IUser;

public interface IClientService extends Runnable {
	public IUser getSelfUser();
}
