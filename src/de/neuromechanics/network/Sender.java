package de.neuromechanics.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

public class Sender implements Runnable {
	public static final String SENDER_TAG = "RPG_PLAYERDATA";

	private ConcurrentHashMap<InetAddress, NetworkPlayer> hm_clients;
	private DatagramSocket socket;
	private DatagramPacket sendPacket;
	private boolean running = true;
	private InetAddress address;
	private InetAddress localAddr;
	private String strData;

	public Sender(ConcurrentHashMap<InetAddress, NetworkPlayer> hm_clients) {
		this.hm_clients = hm_clients;
	}

	@Override
	public void run() {
		System.out.println("<<<<RPG>>>>:  Starting sender thread");
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			localAddr = InetAddress.getLocalHost();
		} catch (SocketException | UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		while(running) {
			for(InetAddress addr : hm_clients.keySet()) {
				if(addr.getHostAddress().equals(localAddr.getHostAddress())) {
					continue;
				}
				try {
					StringBuilder sb = new StringBuilder(SENDER_TAG);
					sb.append(strData);
					byte[] txBuf = sb.toString().getBytes();
					System.out.println("Buffer: " + new String(txBuf, "UTF-8"));
					address = InetAddress.getByName(addr.getHostAddress());
					sendPacket = new DatagramPacket(txBuf, txBuf.length, address, Broadcast.COMM_PORT);
					socket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
			try {
				Thread.sleep(14);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		socket.disconnect();
		socket.close();
		System.out.println("<<<<RPG>>>>:  Sender thread stopped");
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void setData(String s) {
		this.strData = s;
	}
}
