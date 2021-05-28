
package Logic;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author mario
 */
public class ListenClient extends Thread{

    private Socket socket;
    private PrintWriter send;
    private BufferedReader receive;
    private DataOutputStream dos;
    private BufferedOutputStream bos;
    
    public ListenClient(Socket socket){
        this.socket = socket;
    }
    
    public void run(){
        try{
            String dataReceive;
            this.send = new PrintWriter(socket.getOutputStream(),true);   
            this.receive = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
                dataReceive = receive.readLine();
                System.out.println("Recibe: "+dataReceive);
                send.println("Te conectaste");
            while(true){
                dataReceive = receive.readLine();
                System.out.println("Recibe: "+dataReceive);
                send.println("Te conectaste");
            }
        }catch(IOException e){
            System.out.println("Logic.ListenClient.run()"+e);
        }
    }
}
