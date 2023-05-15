package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.storage.IStore;

import java.io.IOException;

public class ClearAllHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;

    public ClearAllHandler(IStore<QuestionAnswerEntry> data) {
        this.data = data;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equals("DELETE")) {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }

        String result = String.valueOf(data.clearAll());
        data.save();
        httpExchange.sendResponseHeaders(200, result.length());
        httpExchange.getResponseBody().write(result.getBytes());
        httpExchange.close();
    }
}
