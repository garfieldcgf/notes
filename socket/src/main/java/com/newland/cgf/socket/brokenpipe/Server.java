package com.newland.cgf.socket.brokenpipe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
	        try {
	            ServerSocket ss = new ServerSocket(3113);
	            Socket s = ss.accept();
	            //client->server
				DataInputStream is = new DataInputStream(s.getInputStream());
	            byte[] buf =new byte[1024];
	            int len = is.read(buf);
	            System.out.println("recv:"+new String(buf,0,len));
	
	            Thread.sleep(10000);

				//server->client
	            s.getOutputStream().write("1111 server".getBytes());
	


                //client->server
				DataInputStream in = new DataInputStream(s.getInputStream());
				byte[] bytes = new byte[1024];
				int length = in.read(bytes);

//				try {
//					System.out.println("recv2:"+new String(bytes,0,length));
//				} catch (Exception e) {
//					e.printStackTrace();
//
//					System.out.println("--------------");

					DataOutputStream out = new DataOutputStream(s.getOutputStream());
					try {
						out.write("error write ".getBytes());
					} catch (IOException e1) {
						System.out.println("==============================");
						e1.printStackTrace();
					}
//				}

			}catch (Exception e){
				System.out.println(e.getStackTrace());
	            e.printStackTrace();
	        }
	    }
}

