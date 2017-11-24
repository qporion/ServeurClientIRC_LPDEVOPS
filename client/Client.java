package client;

import java.net.*;
import java.io.*;

import auth.Session;

public class Client {
    public String address;
    public int port;
    public Socket socket;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    public Session session;
    
    public Client(String address, int port){
        this.address = address;
        this.port = port;
        this.session = new Session(null, 0);
        
        try {
            this.socket = new Socket(address, port);
            
            Thread rec = new Thread(new ClientReceive(this, this.socket));
            Thread send = new Thread(new ClientSend(this, this.socket));
            
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
    
    public Session getSession() {
    	return this.session;
    }
    
    public void setSession (Session session) {
    	this.session = session;
    }
    
}
