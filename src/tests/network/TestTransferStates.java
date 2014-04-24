package tests.network;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;

import mediator.Mediator;
import network.Network;
import network.Transfer;
import network.State;
import network.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.IFile;
import common.IUser;
import common.LocalUser;

/** Inject motherfucking dependencies **/
class TestableTransfer extends Transfer {

	public TestableTransfer(Type type, String file, Network network)
			throws FileNotFoundException {
		super(type, file, network);
	}
	
	void setState(State state) {
		this.state = state;
	}
	
	State getState() {
		return this.state;
	}
	
	void setRemaining(long remaining) {
		this.remaining = remaining;
	}
	
	void setFileSize(long filesize) {
		this.fileSize = filesize;
	}
	
	long getRemaining() {
		return remaining;
	}
	
	void setSrc(FileInputStream f) {
		this.src = f;
	}
	
}

/** Mock network default implementation **/
class NetowrkMock extends Network {

	public NetowrkMock(Mediator med) throws IOException {
		super(med);
	}
	
}

/** Mock network mediator **/
class MediatorMock extends Mediator {
	LocalUser usr = new LocalUser("test", "root/test/", 0xb00b135);

	@Override
	public LocalUser getSelfUser() {
		return usr;
	}
	
	/* Gui from Network specific actions */
	@Override
	public void addDownload(IUser src, IUser dest, IFile file) {
		// to nothing
	}
	@Override
	public void setDownloadProgress(IUser src, IUser dest, IFile file, int progress) {
		// to nothing
	}
}

class ChannelMock extends SocketChannel {
	public ByteBuffer readBuffer;
	public ByteBuffer writeBuffer;
	public ChannelMock() {
		super(null);
		readBuffer = ByteBuffer.allocate(Network.CHUNK_SIZE);
		writeBuffer = ByteBuffer.allocate(Network.CHUNK_SIZE);	
	}
	
	
	@Override
	public SocketAddress getLocalAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getOption(SocketOption<T> arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<SocketOption<?>> supportedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel bind(SocketAddress local) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean connect(SocketAddress remote) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finishConnect() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnectionPending() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		int initial = readBuffer.remaining();
		dst.put(readBuffer);
		return initial - readBuffer.remaining();
	}

	@Override
	public long read(ByteBuffer[] dsts, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> SocketChannel setOption(SocketOption<T> name, T value)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownInput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SocketChannel shutdownOutput() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket socket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int initial = writeBuffer.remaining();
		writeBuffer.put(src);
		return initial - writeBuffer.remaining();
	}

	@Override
	public long write(ByteBuffer[] srcs, int offset, int length)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void implCloseSelectableChannel() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void implConfigureBlocking(boolean block) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
}

/** Mock selection key to return a given channel **/
class SelectionKeyMock extends SelectionKey {
	private SelectableChannel channel;
	boolean cancelled;

	public SelectionKeyMock(SelectableChannel channel) {
		this.channel = channel;
	}
	
	public void reset() {
		cancelled = false;
	}
	
	@Override
	public SelectableChannel channel() {
		return channel;
	}

	@Override
	public void cancel() {
		cancelled = true;
	}

	@Override
	public int interestOps() {
		return 0;
	}

	@Override
	public SelectionKey interestOps(int ops) {
		return null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public int readyOps() {
		return 0;
	}

	@Override
	public Selector selector() {
		return null;
	}
}


public class TestTransferStates {
	Network network;
	Mediator mediator;
	ChannelMock channel;
	SelectionKeyMock key;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mediator = new MediatorMock();
		network = new NetowrkMock(mediator);
		channel = new ChannelMock();
		key = new SelectionKeyMock(channel);
		
		File dir = new File("root/test");
		if (!dir.exists())
			dir.mkdir();
	}
	
	public static boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File dir = new File("root/test");
		deleteDirectory(dir);
	}

	@Test
	public void testAcceptRequestCreatesFile() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.upload, "dummy", network);
			t.setState(State.acceptRequest);
			channel.readBuffer.clear();
			channel.readBuffer.put("testfile:testuser".getBytes());
			while(channel.readBuffer.hasRemaining())
				channel.readBuffer.put((byte) 0);
			channel.readBuffer.flip();
			FileOutputStream testFile = new FileOutputStream("root/test/testfile");
			testFile.write("The cake is a lie".getBytes());
			testFile.close();
			t.doRead(key);
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (IllegalBlockingModeException e) {
			assertTrue(t.getState() == State.uploadBegin);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testAcceptRequestRemainingPartial() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.upload, "dummy", network);
			t.setState(State.acceptRequest);
			channel.readBuffer.clear();
			channel.readBuffer.put("testfile:testuser".getBytes());
			while(channel.readBuffer.hasRemaining())
				channel.readBuffer.put((byte) 0);
			t.doRead(key);
			
			/* Buffer still has remaining, should wait for more */
			assertTrue(t.getState() == State.acceptRequestRemaining);
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testReadWaitFileSize() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.download, "dummy", network);
			t.setState(State.waitFileSize);
			channel.readBuffer.clear();
			channel.readBuffer.putLong(42);
			channel.readBuffer.flip();
			t.doRead(key);
			/* Buffer still has remaining, should wait for more */
			assertTrue(t.getState() == State.downloading);
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testReadDownloadingComplete() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.download, "dummy2", network);
			t.setState(State.downloading);
			byte[] content = "anaaremere".getBytes();
			t.setFileSize(content.length);
			t.setRemaining(content.length);
			channel.readBuffer.clear();
			channel.readBuffer.put(content);
			channel.readBuffer.flip();
			t.doRead(key);
			/* Successful download */
			assertTrue(t.getRemaining() == 0);
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}

	@Test
	public void testReadDownloadingClosesSelectionKey() {
		TestableTransfer t = null;
		try {
			key.reset();
			t = new TestableTransfer(Type.download, "dummy2", network);
			t.setState(State.downloading);
			byte[] content = "anaaremere".getBytes();
			t.setFileSize(content.length);
			t.setRemaining(content.length);
			channel.readBuffer.clear();
			channel.readBuffer.put(content);
			channel.readBuffer.flip();
			t.doRead(key);
			/* Selection key cancelled */
			assertTrue(key.cancelled);
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testWriteDownloadRequest() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.download, "downloadFile", network);
			t.setState(State.sendRequest);
			channel.writeBuffer.clear();
			t.doWrite(key);

		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (IllegalBlockingModeException e) {
			// Written according to protocol
			channel.writeBuffer.flip();
			assertTrue("downloadFile:test".equals(new String(channel.writeBuffer.array()).split("\0")[0]));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testWriteUploadBegin() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.upload, "downloadFile", network);
			t.setState(State.uploadBegin);
			channel.writeBuffer.clear();
			
			File f = new File("root/test/downloadFile");
			FileOutputStream fos = new FileOutputStream(f);
			byte[] content = "anaaremultemere".getBytes();
			fos.write(content);
			fos.close();
			FileInputStream fin = new FileInputStream(f);
			t.setSrc(fin);
			t.setFileSize(content.length);
			t.setRemaining(content.length);
			t.doWrite(key);

			channel.writeBuffer.flip();
			long size = channel.writeBuffer.getLong();
			assertTrue(size == content.length);
			
			byte[] result = new byte[content.length];
			channel.writeBuffer.get(result);
			assertTrue(Arrays.equals(result, content));
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testWriteUploading() {
		TestableTransfer t = null;
		try {
			t = new TestableTransfer(Type.upload, "downloadFile", network);
			t.setState(State.uploading);
			channel.writeBuffer.clear();
			
			File f = new File("root/test/downloadFile");
			FileOutputStream fos = new FileOutputStream(f);
			byte[] content = "anaaremultemere".getBytes();
			fos.write(content);
			fos.close();
			FileInputStream fin = new FileInputStream(f);
			t.setSrc(fin);
			t.setFileSize(content.length);
			t.setRemaining(content.length);
			t.doWrite(key);

			channel.writeBuffer.flip();
			
			byte[] result = new byte[content.length];
			
			channel.writeBuffer.get(result);
			assertTrue(Arrays.equals(result, content));
			
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Bad state found in Transfer");
		}
	}
}
