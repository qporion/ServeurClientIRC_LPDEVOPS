
package Client;

import java.io.PrintWriter;
import java.util.Scanner;

public class ClientSend implements Runnable {
    public PrintWriter out;
    
    public ClientSend (PrintWriter out){
        this.out = out;  
    }
    
    @Override
    public void run() {
        Scanner sc = new Scanner (System.in);
        while(true){
            System.out.print ("Votre message >> ");
            String msg = "B%"+sc.nextLine(); // P%id% envoi en privé
            out.println(msg);
            out.flush();
        }
    }
}
