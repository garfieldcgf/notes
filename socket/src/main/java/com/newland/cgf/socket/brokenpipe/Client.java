package com.newland.cgf.socket.brokenpipe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        Socket s = null;
        try {
            s = new Socket("127.0.0.1", 3113);
            s.setSoTimeout(5000);

            DataOutputStream out = new DataOutputStream(
                    s.getOutputStream());


            //client->server
            out.write("1111 from client".getBytes());


            // server->client
            DataInputStream in = new DataInputStream(s.getInputStream());
            byte[] bytes = new byte[1024];
            int len = in.read(bytes);

            System.out.println("client:" + new String(bytes, 0, len));

            out.write("2222 from client".getBytes());


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

