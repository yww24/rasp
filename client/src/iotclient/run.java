package iotclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

public class run extends Thread {	
	public void run() {
		while(true) {
			try {
				InputStreamReader in = new InputStreamReader(client.sock.getInputStream());
				String str;				
				BufferedReader bf = new BufferedReader(in);
				
				in = new InputStreamReader(client.sock.getInputStream());
				str = bf.readLine();
				if(str.equals("warning")) {
					connected.changestatus("허가받지 않은 사람이 침입했습니다.");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
