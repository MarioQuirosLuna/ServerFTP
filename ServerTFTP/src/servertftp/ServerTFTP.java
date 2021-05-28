
package servertftp;

import Logic.Server;


/**
 *
 * @author mario
 */
public class ServerTFTP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Server server = new Server(69);
        server.start();
    }
    
}
