package tests.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.IFile;
import common.IUser;

public class UserFactory {
	int userno = 0;
	FileFactory f = new FileFactory();
	
	static Random random = new Random();
	private class UserMock implements IUser {
		String name;
		ArrayList<IFile> files;
		
		
		public UserMock(String name) {
			this.name = name;
			this.files = new ArrayList<IFile>();
			
			int n = random.nextInt(20);
			
			//n = 5;
			for (int i = 0; i < n + 1; i++) {
				files.add(f.produce());
			}
		}


		@Override
		public String getName() {
			return name;
		}

		@Override
		public List<IFile> getFiles() {
			return files;
		}
		
		public String toString() {
			return getName();
		}
	}
	
	public IUser produce() {
		userno++;
		return new UserMock("User" + userno);
	}
	
	public IUser produce(String name) {
		return new UserMock(name);
	}
}
