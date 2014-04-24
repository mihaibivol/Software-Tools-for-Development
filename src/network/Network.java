package network;

import java.io.IOException;
import java.net.InetSocketAddress;
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
	Selector selector;
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
			Transfer t = new Transfer(Type.download, file.getName(), this);
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
		channel.register(key.selector(), SelectionKey.OP_READ, new Transfer(Type.upload, null, this));
	}

	@Override
	protected void process(List<Object> chunks) {
	}
}