package sayit.server.contexts;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class ISendHandler implements HttpHandler {

    public abstract void handle(HttpExchange httpExchange) throws IOException;
    
}
