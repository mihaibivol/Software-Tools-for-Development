package tests.factories;

import common.IFile;


public class FileFactory {
	int fileno = 0;
	
	private class FileMock implements IFile {
		String name;
		
		public FileMock(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public IFile produce() {
		fileno++;
		return new FileMock("File" + fileno);
	}
}
