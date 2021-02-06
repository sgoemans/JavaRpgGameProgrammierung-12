package de.neuromechanics.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Broadcasts a tag to let other players know of one's existence. When other player PCs receive
 * such a broadcast, they add this player to their player collection hashmap.
 *  
 * @author sgoemans
 *
 */
public class Broadcast implements Runnable {
	public static final String BROADCAST_ADDRESS = "192.168.1.255";
	public static final String BROADCAST_TAG = "RPG_BROADCAST";
	public static final int COMM_PORT = 4445;
	public static final int BROADCAST_DELAY = 3000;
	
	private boolean running = true;
	private DatagramSocket socket;
	private DatagramPacket packet;
	
	@Override
	public void run() {
		System.out.println("<<<<RPG>>>>:  Starting broadcasting thread");
		byte[] bytes = BROADCAST_TAG.getBytes();
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			InetAddress address = InetAddress.getByName(BROADCAST_ADDRESS);
			packet = new DatagramPacket(bytes, bytes.length, address, COMM_PORT);
		} catch (SocketException | UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		while(running) {
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(int i = 0; i < 10; ++i) {
				try {
					if(!running) {
						break;
					}
					Thread.sleep(BROADCAST_DELAY / 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("<<<<RPG>>>>:  Broadcast thread stopped");
		socket.disconnect();
		socket.close();
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
}