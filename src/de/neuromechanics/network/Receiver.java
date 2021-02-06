package de.neuromechanics.network;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The receiver thread waits for incoming packets on its ip address and port. Incoming data consists of 
 * network player entity x/y positions. This data is used to update the character's position on the playground. 
 * 
 * @author sgoemans
 *
 */
public class Receiver implements Runnable {
	private ConcurrentHashMap<InetAddress, NetworkPlayer> hm_clients;
	private DatagramSocket socket;
	private DatagramPacket receivePacket;
	private boolean running = true;
	private INetworkPlayer networkPlayerFactory;

	public Receiver(INetworkPlayer networkPlayerFactory, ConcurrentHashMap<InetAddress, NetworkPlayer> hm_clients) {
		this.networkPlayerFactory = networkPlayerFactory;
		this.hm_clients = hm_clients;
	}

	@Override
	public void run() {
		InetAddress localAddr;
		System.out.println("<<<<RPG>>>>: Starting receiver thread");
		try {
			socket = new DatagramSocket(Broadcast.COMM_PORT);
			socket.setBroadcast(true);
			localAddr = InetAddress.getLocalHost();
		} catch (SocketException | UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}

		while(running) {
			try {
				byte[] recvBuf = new byte[32];
				receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(receivePacket);
			} catch (SocketException e1) {
				System.out.println("<<<<RPG>>>>: Receiver socket closed");
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			/* Don't process packets that originated from this computer */
			if(receivePacket.getAddress().getHostAddress().equals(localAddr.getHostAddress())) {
				continue;
			}

			NetworkPlayer networkPlayer;
			String message = new String(receivePacket.getData()).trim();
			if (message.equals(Broadcast.BROADCAST_TAG)) {
				if(hm_clients.containsKey(receivePacket.getAddress())) {
					networkPlayer = hm_clients.get(receivePacket.getAddress());
					networkPlayer.setLastActiveInMillies(System.currentTimeMillis());
				} else {
					networkPlayer = networkPlayerFactory.createNetworkPlayer(receivePacket.getSocketAddress().hashCode(),
							System.currentTimeMillis());
					System.out.println("<<<<RPG>>>>: Created new network player: " + receivePacket.getSocketAddress());
				}
				hm_clients.put(receivePacket.getAddress(), networkPlayer);
			} else {
				String[] strArr = message.split(";");
				if(strArr[0].equals(Sender.SENDER_TAG)) {
					networkPlayer = hm_clients.get(receivePacket.getAddress());
					if(networkPlayer != null) {
						Point p = new Point();
						p.x = Integer.parseInt(strArr[1]);
						p.y = Integer.parseInt(strArr[2]);
						networkPlayer.setMove(p);
						networkPlayer.setEntityX(Integer.parseInt(strArr[3]));
						networkPlayer.setEntityY(Integer.parseInt(strArr[4]));
					}
				}
			}
		}
		socket.disconnect();
		socket.close();
		System.out.println("<<<<RPG>>>>:  Receiver thread stopped");
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	/*
	 * If a thread is waiting on a known socket, the socket can be closed to cause the thread to return immediately.
	 */
	public void stop() {
		socket.close();
	}

}