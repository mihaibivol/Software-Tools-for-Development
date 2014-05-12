package clientservice;

import common.LocalUser;

public interface IClientService {
	public LocalUser getSelfUser();
	
	public void updateSelfFiles();
	
	public void exit();
}
