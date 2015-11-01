package com.team766.rrb4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class VRConnector implements Runnable{
	public static final boolean SIMULATOR = true;
	private static VRConnector instance_;

	
	// Command indexes
	public static final int RESET_SIM = 0;

	public static final int LEFT_MOTOR = 10;
	public static final int RIGHT_MOTOR = 11;
	public static final int INTAKE = 12;
	public static final int LAUNCH = 13;

	private static final int MAX_COMMANDS = 64;

	// Feedback indexes
	public static final int LEFT_ENCODER = 10;
	public static final int RIGHT_ENCODER = 11;
	public static final int HEADING = 12;
	public static final int INTAKE_STATE = 13;
	public static final int BALL_PRESENCE = 14;

	private static final int commandsPort = 7661;
	private static final int feedbackPort = 7662;
	private static final int BUF_SZ = 1024;

	Selector selector;
	InetSocketAddress sendAddr;
	ByteBuffer feedback = ByteBuffer.allocate(BUF_SZ);
	ByteBuffer commands = ByteBuffer.allocate(BUF_SZ);

	public static VRConnector getInstance() {
		if (instance_ == null)
			try {
				instance_ = new VRConnector();
			} catch (IOException e) {
				System.out.println("Failed to start Simmulator");
			}
		return instance_;
	}
	
	public int getFeedback(int index) {
		return feedback.getInt(index * 4);
	}

	public void putCommand(int index, int value) {
		commands.putInt(index * 4, value);
	}

	public void putCommandFloat(int index, float value) {
		putCommand(index, (int) (value * 512.0f));
	}

	public void putCommandBool(int index, boolean value) {
		putCommand(index, value ? 511 : -512);
	}

	public VRConnector() throws IOException {
		selector = Selector.open();
		DatagramChannel channel = DatagramChannel.open();
		InetSocketAddress receiveAddr = new InetSocketAddress(feedbackPort);
		channel.bind(receiveAddr);
		sendAddr = new InetSocketAddress(InetAddress.getLoopbackAddress(),
				commandsPort);
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_READ);
		commands.limit(MAX_COMMANDS * 4);
		commands.order(ByteOrder.LITTLE_ENDIAN);
		feedback.order(ByteOrder.LITTLE_ENDIAN);
	}

	public boolean process() throws IOException {
		selector.selectedKeys().clear();
		selector.selectNow();
		boolean newData = false;
		for (SelectionKey key : selector.selectedKeys()) {
			if (!key.isValid()) {
				continue;
			}

			DatagramChannel chan = (DatagramChannel) key.channel();
			if (key.isReadable()) {
				feedback.clear();
				chan.receive(feedback);
				newData = true;
				key.interestOps(SelectionKey.OP_WRITE);
			} else if (key.isWritable()) {
				chan.send(commands.duplicate(), sendAddr);
				putCommand(RESET_SIM, 0);
				key.interestOps(SelectionKey.OP_READ);
			}
		}
		return newData;
	}
	
	public void run(){
		while(true){
			try {
				process();
				Thread.sleep(33);
			}catch(Exception e){
			}
		}
	}
}