package sayit.server;

import sayit.storage.*;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class webServer {
    // initialize server port and hostname
    private static final int SERVER_PORT = 8100;
    private static final String SERVER_HOSTNAME = "localhost";


    public static void main(String[] args) throws IOException {
        // create a thread pool to handle requests
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) 
        Executors.newFixedThreadPool(10);

        // create a TsvStore to store data
        TsvStore serverStorage = TsvStore.createOrOpenStore("entries.tsv");


        // create a server
        HttpServer server = HttpServer.create(
        new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT),
        0
        );

        server.createContext("/",  new RequestHandler(serverStorage));
        server.setExecutor(threadPoolExecutor);
        server.start();


        System.out.println("Server started on port " + SERVER_PORT);

    }
}
