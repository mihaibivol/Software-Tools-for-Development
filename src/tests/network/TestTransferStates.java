package tests.network;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import mediator.Mediator;
import network.Network;
import network.Transfer;
import network.State;
import network.Type;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	
}

/** Mock network default implementation **/
class NetowrkMock extends Network {

	public NetowrkMock(Mediator med) throws IOException {
		super(med);
	}
	
}

/** Mock network mediator **/
class MediatorMock extends Mediator {
	
}

class ChannelMock extends SocketChannel {
	public ChannelMock() {
		super(null);
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
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
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

	public SelectionKeyMock(SelectableChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public SelectableChannel channel() {
		return channel;
	}

	@Override
	public void cancel() {
		
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
	SocketChannel channel;
	SelectionKey key;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		mediator = new MediatorMock();
		network = new NetowrkMock(mediator);
		channel = new ChannelMock();
		key = new SelectionKeyMock(channel);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadAcceptRequestCreatesFile() {
		try {
			TestableTransfer t = new TestableTransfer(Type.upload, "testFile", network);
			t.setState(State.acceptRequest);
			t.doRead(key);
			
		} catch (FileNotFoundException e) {
			fail("File not found during stateTransfer initialization");
		} catch (Exception e) {
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testReadAcceptRequestRemaining() {
		try {
			Transfer t = new Transfer(Type.upload, "testFile", network);
			t.doRead(key);
		} catch (FileNotFoundException e) {
			fail("File not found during Transfer initialization");
		} catch (Exception e) {
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testReadWaitFileSize() {
		try {
			Transfer t = new Transfer(Type.upload, "testFile", network);
			t.doRead(key);
		} catch (FileNotFoundException e) {
			fail("File not found during Transfer initialization");
		} catch (Exception e) {
			fail("Bad state found in Transfer");
		}
	}
	
	@Test
	public void testReadDownloading() {
		try {
			Transfer t = new Transfer(Type.upload, "testFile", network);
			t.doRead(key);
		} catch (FileNotFoundException e) {
			fail("File not found during Transfer initialization");
		} catch (Exception e) {
			fail("Bad state found in Transfer");
		}
	}

}
