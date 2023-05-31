package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Handles a GET request for checking if the server is up.
 */
public class PingHandler implements HttpHandler {
    /**
     * Handles the request.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Technically, we don't really need this, but might as well add it.
        if (!exchange.getRequestMethod().equals("GET")) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.close();
            return;
        }

        System.out.println("Received GET request for /ping");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        exchange.close();
    }
}
