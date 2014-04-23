package common;

import java.util.List;

public interface IUser {
	public String getName();
	
	public List<IFile> getFiles();
	public String toString();
}
