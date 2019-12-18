package iotclient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import javax.swing.*;
import se.datadosen.component.RiverLayout;

public class connected implements ActionListener , Runnable {
	JFrame frame = new JFrame();
	JLabel label = new JLabel("보안장치가 해제되었습니다.");
	static JLabel status = new JLabel("현재 들어온 사람이 없습니다.");
	
	JButton open = new JButton("보안해제");
	JButton close = new JButton("보안설정");
	
	int distance;
	
	public connected (int distance) {
		this.distance = distance;
	}
	
	public void run() {
		JPanel pane = new JPanel(new RiverLayout());
		
		
		JLabel stat = new JLabel("서버와 연결되었습니다. 경계 거리 :" + distance);
		
		open.addActionListener(this);
		close.addActionListener(this);
		
		pane.add("center", stat);
		pane.add("br center", status); 
		pane.add("br br center",label);
		pane.add("br br center", open);
		pane.add(close);
		
		frame.add(pane);
		frame.setSize(300,300);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					client.writer.close();
					client.sock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				frame.dispose();
			}
		});

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource().equals(open)) {
			client.writer.println("1");
			label.setText("보안장치가 해제되었습니다.");
		}
		
		if(e.getSource().equals(close)) {
			client.writer.println("2");
			label.setText("보안장치가 활성화되었습니다.");
		}
	}
	public static void changestatus(String str) {
		status.setText(str);
	}





}
