package client;

import java.net.*;
import java.io.*;



public class Client {
    public String address;
    public int port;
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    
    public Client(String address, int port){
        this.address = address;
        this.port = port;
        try {
            this.socket = new Socket(address, port);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream());
            
            Thread rec = new Thread(new ClientReceive(this, this.in));
            Thread send = new Thread(new ClientSend(this.out));
            
            rec.start();
            send.start();
        } catch(IOException e)  {
            e.printStackTrace();
        }
        
        
    }
    
    public void disconnectedServer(){
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
        
        
    }
    
}
