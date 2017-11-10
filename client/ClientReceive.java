
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;


public class ClientReceive implements Runnable {
    public Client client;
    public BufferedReader in;
    
    public ClientReceive (Client client, BufferedReader in){
        this.client = client;
        this.in = in;
    }
    
    @Override
    public void run() {
       boolean isActive = true;
       while (isActive) {
           String message = null;
           try {
                message = in.readLine(); 
           }
           catch (IOException e) {
               e.printStackTrace();
               
           }
           
           
           if (message != null){
               System.out.println ("\nMessage reçu : " + message);
           } else {
               isActive = false;
           }
       }
       client.disconnectedServer();
    }
    
}
