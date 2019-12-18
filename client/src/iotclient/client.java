package iotclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;

import javax.swing.*;
import se.datadosen.component.RiverLayout;

public class client implements ActionListener {
	
	JButton submit = new JButton("연결");
	JTextField ip = new JTextField(20);
	static Socket sock;
	static PrintWriter writer;
	static JSlider slide = new JSlider();
	
	public client() {
		JPanel pane = new JPanel(new RiverLayout());
		JFrame frame = new JFrame();
		
		
		
		
		slide.setMinorTickSpacing(10);
		slide.setPaintTicks(true);
		slide.setPaintLabels(true);
		slide.setLabelTable(slide.createStandardLabels(20));
		
		submit.addActionListener(this);
		
		pane.add(new JLabel("ip주소"));
		pane.add("center",ip);
		pane.add("br center",new JLabel("거리값"));
		pane.add(slide);
		pane.add("br center",submit);
		
		frame.add(pane);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void go(String ipAddr) {
		try {
			sock = new Socket(ipAddr,8080);
			writer = new PrintWriter(sock.getOutputStream(),true);
		} catch(Exception e) {
			System.out.println("Error writing to socket");
		}
		try {
			writer.println(String.valueOf(slide.getValue()));
			
		} catch (Exception e) {
			System.out.println("Error writing to socket");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(submit)) {
			client.go(ip.getText());
			connected con = new connected(slide.getValue());
			new Thread(con).start();

		}
		
	}
	
}
