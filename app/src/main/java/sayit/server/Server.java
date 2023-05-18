package sayit.server;

import com.sun.net.httpserver.HttpServer;
import sayit.common.qa.QuestionAnswerEntry;
import sayit.server.contexts.*;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;
import sayit.server.storage.IStore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The server class that handles all the requests.
 */
public class Server {
    private final HttpServer _server;

    private final int _port;

    /**
     * Creates a new server.
     *
     * @param storage The storage to use.
     * @param host    The host to use.
     * @param port    The port to use.
     * @param whisper The <c>whisper</c> instance to use.
     * @param chatgpt The <c>ChatGPT</c> instance to use.
     */
    public Server(IStore<QuestionAnswerEntry> storage, String host, int port, IWhisper whisper, IChatGpt chatgpt) {
        this._port = port;

        try {
            this._server = HttpServer.create(
                    new InetSocketAddress(host, port),
                    0
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(10);
        this._server.createContext("/ask", new AskQuestionHandler(storage, whisper, chatgpt));
        this._server.createContext("/history", new HistoryHandler(storage));
        this._server.createContext("/delete-question", new DeleteHandler(storage));
        this._server.createContext("/clear-all", new ClearAllHandler(storage));
        this._server.createContext("/ping", new PingHandler());
        this._server.setExecutor(threadPoolExecutor);
    }

    /**
     * Starts the server. It is recommended that this is called in a separate thread.
     */
    public void start() {
        this._server.start();
        System.out.println("Server started on port " + this._port);
    }
}
