import helpers.CreateUsersMap;
import helpers.ServerToClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;

public class Main {
    private final int BUFFER_LENGHT = 4096;

    public static void main(String[] args) throws IOException {
        Logger logger = new helpers.Logger().getLogger();
        new CreateUsersMap();
        LinkedHashSet<supportive.MusicBand> collection = new LinkedHashSet<>();
        ServerToClient listener = new ServerToClient(InetAddress.getByName("127.0.0.1"), collection); //start server
        logger.log(Level.INFO, "SERVER STARTED");
        while (true){
            listener.listen(); //listen client and send response

        }
    }
}
