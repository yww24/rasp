import java.io.*;
import java.net.*;
import java.util.*;

public class SocketClient {
	PrintWriter writer;
	Socket sock;
	Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage : java SocketClient IP_Address");
			return;
		}
		SocketClient client = new SocketClient();
		client.go(args[0]);
	}

	private void go(String ipAddr) {
		// Setting up network
		try {
			sock = new Socket(ipAddr, 8080);
			writer = new PrintWriter(sock.getOutputStream(), true);
		} catch(Exception ex) {
			System.out.println("Error opening a socket");
		}
		try {
			while (true) {
				System.out.print("Enter a number : ");
				String s = scan.next();
				writer.println(s);
				if (s.equals("6")) break;
			}
			writer.close();
			sock.close();
		} catch(Exception ex) {
			System.out.println("Error writing to socket");
		}
	}
}

