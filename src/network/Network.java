package network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingWorker;

import mediator.Mediator;
import common.IFile;
import common.IUser;
import common.SimpleFile;

import org.apache.log4j.*;

enum State {
	sendRequest,
	sendRequestRemaining,
	waitFileSize,
	acceptRequest,
	acceptRequestRemaining,
	downloading,
	uploadBegin,
	uploading
};

enum Type {
	upload,
	download
}

public class Network extends SwingWorker<Object, Object> implements INetwork {
	static Logger logger = Logger.getLogger(Network.class);
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	public static int CHUNK_SIZE = 8192;
	Mediator med;
	boolean running = false;
	Set<String> requests = new TreeSet<String>();
	Queue<RegisterEntry> registerQueue = new ConcurrentLinkedQueue<RegisterEntry>();
	
	@Override
	public void downloadFile(IFile file, IUser user) {
		// TODO Auto-generated method stub

		NetworkUser owner = (NetworkUser) user;
		// prevent duplicates
		String request = file.getName() + "@" + user.getName();
		if (requests.contains(request))
			return;
		requests.add(request);
		logger.info("Target is: " + owner.getAddress());


		try {
			Transfer t = new Transfer(Type.download, file.getName());
			med.addDownload(user, med.getSelfUser(), file);
			t.setOtherUser(user);
			t.setFile(file);

			SocketChannel socket = SocketChannel.open();
			socket.configureBlocking(false);
			boolean connected = socket.connect(owner.getAddress());
			logger.info("register.");
			if (!connected) {
				registerQueue.add(new RegisterEntry(socket, SelectionKey.OP_CONNECT, t));
			} else {
				registerQueue.add(new RegisterEntry(socket, SelectionKey.OP_WRITE, t));
			}
			selector.wakeup();
		} catch (Exception e) {
			logger.info("error");
			e.printStackTrace();
		}
	}
	
	private class RegisterEntry {
		public SocketChannel channel;
		public int op;
		public Object attachment;
		public RegisterEntry(SocketChannel channel, int opWrite, Object attachment) {
			this.channel = channel;
			this.op = opWrite;
			this.attachment = attachment;
		}
	};
	
	private class Transfer {
		Logger logger = Logger.getLogger(Transfer.class);
		State state;
		
		FileInputStream src;
		FileOutputStream dst;
		ByteBuffer buffer;
		String filename;
		long remaining = 0;
		long fileSize = 0;
		IUser otherUser;
		IFile transferredFile;
		
		void setOtherUser(IUser usr) {
			otherUser = usr;
		}
		void setFile(IFile file) {
			transferredFile = file;
		}
		
		public Transfer(Type type, String file) throws FileNotFoundException {
			if (type == Type.upload) {
				state = State.acceptRequest;
			} else {
				state = State.sendRequest;
				dst = new FileOutputStream(med.getSelfUser().getDownloadLocation() + file);
				filename = file;
			}
			buffer = ByteBuffer.allocate(CHUNK_SIZE);
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
					buffer.put((byte) 0); // TODO
				buffer.flip();				
			case sendRequestRemaining:
				channel.write(buffer);
				if (buffer.hasRemaining()) {
					state = State.sendRequestRemaining;
					break;
				}
				state = State.waitFileSize;
				channel.register(selector, SelectionKey.OP_READ, this);
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
				channel.register(selector, SelectionKey.OP_WRITE, this);
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
	
	public Network(Mediator med) throws IOException {
		this.med = med;
		med.registerNetwork(this);
	}

	@Override
	protected Object doInBackground() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(med.getSelfUser().getPort()));
			running = true;
			logger.info("Listening on: " + med.getSelfUser().getPort());
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
			while (running) {
				logger.info("Registerqueue emtpy:" + registerQueue.isEmpty());
				while (!registerQueue.isEmpty()) {
					logger.info("registering");
					RegisterEntry entry = registerQueue.poll();
					entry.channel.register(selector, entry.op, entry.attachment);
				}
				selector.select();
				Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
				while(keyIterator.hasNext()) {
				    SelectionKey key = keyIterator.next();
	
				    if(key.isAcceptable()) {
				    	accept(key);
				    } else if (key.isConnectable()) {
				    	Transfer t = (Transfer)key.attachment();
				    	SocketChannel channel = (SocketChannel) key.channel();
				    	try {
				    		channel.finishConnect();
				    		channel.register(selector, SelectionKey.OP_WRITE, t);
				    	} catch (Exception e) {
				    		logger.info("Unable to connect.");
				    		key.cancel();
				    	}
				    } else if (key.isReadable()) {
				      Transfer t = (Transfer)key.attachment();
				      t.doRead(key);
				    } else if (key.isWritable()) {
				    	Transfer t = (Transfer)key.attachment();
				    	t.doWrite(key);
				    }
				    keyIterator.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (selector != null)
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			if (serverSocketChannel != null)
				try {
					serverSocketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return null;
	}

	public void accept(SelectionKey key) throws IOException {
		logger.info("accepting.");
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverSocketChannel.accept();
		logger.info("Finished accepting.");
		channel.configureBlocking(false);
		channel.register(key.selector(), SelectionKey.OP_READ, new Transfer(Type.upload, null));
	}

	@Override
	protected void process(List<Object> chunks) {
	}
}