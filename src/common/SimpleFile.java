package common;

public class SimpleFile implements IFile {
	String name;
	
	public SimpleFile(String name) {
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
