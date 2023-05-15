package sayit.server.contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.storage.IStore;

import java.io.IOException;

public class DeleteHandler implements HttpHandler {
    private final IStore<QuestionAnswerEntry> data;

    public DeleteHandler(IStore<QuestionAnswerEntry> data) {
        this.data = data;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("DELETE")) {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        String[] querySplit = query.split("=");
        if (querySplit.length != 2 || !querySplit[0].equals("id")) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(querySplit[1]);
        } catch (NumberFormatException e) {
            exchange.sendResponseHeaders(400, 0);
            exchange.close();
            return;
        }

        if (!data.getEntries().containsKey(id)) {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
            return;
        }

        String result = String.valueOf(data.delete(id));
        data.save();
        exchange.sendResponseHeaders(200, result.length());
        exchange.getResponseBody().write(result.getBytes());
        exchange.close();
    }
}
