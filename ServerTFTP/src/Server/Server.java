
package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author mario
 */
public class Server extends Thread{
    private final int socketPortNumber;
    
    public Server(int socketPortNumber){
        super("Server");
        this.socketPortNumber = socketPortNumber;
    }
    
    @Override
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(this.socketPortNumber);
            System.out.println("***Servidor ejecutandose***\n");
            InetAddress address = InetAddress.getLocalHost();
            String ip = String.valueOf(address);
            ip = ip.split("/")[1];
            JOptionPane.showMessageDialog(null, "Dirección: "+ip+":"+this.socketPortNumber);
            
            do {
                System.out.println("Esperando Clientes...\n");
                new ListenClient(serverSocket.accept()).start();
                System.out.println("Cliente acceptado...");
            } while (true);
        } catch (IOException ex) {
            System.out.println("Server.Server.run() "+ex);
//            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
