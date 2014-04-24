package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import mediator.Mediator;

import org.apache.log4j.Logger;

import common.IFile;
import common.IUser;
import common.SimpleFile;

public class Transfer {
	Logger logger = Logger.getLogger(Transfer.class);
	protected State state;
	
	FileInputStream src;
	FileOutputStream dst;
	ByteBuffer buffer;
	String filename;
	long remaining = 0;
	long fileSize = 0;
	IUser otherUser;
	IFile transferredFile;
	Mediator med;
	Network network;
	
	void setOtherUser(IUser usr) {
		otherUser = usr;
	}
	void setFile(IFile file) {
		transferredFile = file;
	}
	
	public Transfer(Type type, String file, Network network) throws FileNotFoundException {
		this.med = network.med;
		this.network = network;
		if (type == Type.upload) {
			state = State.acceptRequest;
		} else {
			state = State.sendRequest;
			dst = new FileOutputStream(med.getSelfUser().getDownloadLocation() + file);
			filename = file;
		}
		buffer = ByteBuffer.allocate(Network.CHUNK_SIZE);
	}
	
	public void doWrite(SelectionKey key) throws Exception {
		logger.info("Do write.");
		SocketChannel channel = (SocketChannel) key.channel();
		switch (state) {
		case sendRequest:
			buffer.clear();
			buffer.put(filename.getBytes());
			buffer.put((":" + med.getSelfUser().getName()).getBytes());
			while (buffer.hasRemaining())
				buffer.put((byte) 0);
			buffer.flip();				
		case sendRequestRemaining:
			channel.write(buffer);
			if (buffer.hasRemaining()) {
				state = State.sendRequestRemaining;
				break;
			}
			state = State.waitFileSize;
			channel.register(network.selector, SelectionKey.OP_READ, this);
			logger.info("Switched to waitFileSize");
			break;
		case uploadBegin:
			// file size is already in the buffer.
			int read = src.getChannel().read(buffer);
			remaining -= read;
			buffer.flip();
			channel.write(buffer);
			state = State.uploading;
			break;
		case uploading:
			if (buffer.hasRemaining()) {
				channel.write(buffer);
			} else {
				buffer.clear();
				read = src.getChannel().read(buffer);
				buffer.flip();
				if (read == -1) {
					key.cancel();
					channel.close();
					src.close();
					logger.info("upload done.");
					med.setDownloadProgress(med.getSelfUser(), otherUser, transferredFile, 100);
					break;
				}
				remaining -= read;
				float progress = (fileSize - remaining) / (float)fileSize * 100;
				med.setDownloadProgress(med.getSelfUser(), otherUser, transferredFile, (int) progress);
				channel.write(buffer);
			}
			break;
		default:
			throw new Exception("Invalid state");
		}
	}
	
	public void doRead(SelectionKey key) throws Exception {
		logger.info("Do read.");
		SocketChannel channel = (SocketChannel) key.channel();
		switch (state) {
		case acceptRequest:
			buffer.clear();
		case acceptRequestRemaining:
			channel.read(buffer);
			if (buffer.hasRemaining()) {
				state = State.acceptRequestRemaining;
				break;
			}
			// java strings are not null terminated.
			int endPos;
			for (endPos = 0; endPos < buffer.array().length; endPos++)
				if (buffer.array()[endPos] == 0)
					break;
			String request = new String(buffer.array(), 0, endPos);
			String requestPart[] = request.split(":");
			File f = new File(med.getSelfUser().getHome() + requestPart[0]);
			
			// create an upload transfer user
			otherUser = new NetworkUser(requestPart[1], null, 0xdeadbeef);
			transferredFile = new SimpleFile(requestPart[0]);
			med.addDownload(med.getSelfUser(), otherUser, transferredFile);
			
			src = new FileInputStream(f);
			buffer.clear();
			buffer.putLong(f.length());
			fileSize = f.length();
			remaining = fileSize;
			state = State.uploadBegin;
			channel.register(network.selector, SelectionKey.OP_WRITE, this);
			break;
		case waitFileSize:
			logger.info("Wait filesize");
			buffer.clear();
			channel.read(buffer);
			buffer.flip();
			remaining = buffer.getLong();
			fileSize = remaining;
			state = State.downloading;
			while (buffer.hasRemaining()) {
				int written = dst.getChannel().write(buffer);
				remaining -= written;
				float progress = (fileSize - remaining) / (float)fileSize * 100;
				med.setDownloadProgress(otherUser, med.getSelfUser(), transferredFile, (int) progress);
			}
			break;
		case downloading:
			buffer.clear();
			int readSize = channel.read(buffer);
			logger.info("Read: " + readSize);
			if (readSize == -1) {
				key.cancel();
				channel.close();
				dst.close();
				med.setDownloadProgress(otherUser, med.getSelfUser(), transferredFile, 100);
				break;
			}
		
			buffer.flip();
			while(buffer.hasRemaining()) {
				int written = dst.getChannel().write(buffer);
				remaining -= written;
				logger.info("Remaining: "+ remaining);
				float progress = (fileSize - remaining) / (float)fileSize * 100;
				med.setDownloadProgress(otherUser, med.getSelfUser(), transferredFile, (int) progress);
			}
			if (remaining == 0) {
				logger.info("Download completed.");
				key.cancel();
				channel.close();
				dst.close();
			}
			break;
		default:
			throw new Exception("Invalid state");
		}
	}
}

