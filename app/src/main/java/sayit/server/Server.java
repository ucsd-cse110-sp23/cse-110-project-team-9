package sayit.server;

import com.sun.net.httpserver.HttpServer;
import sayit.common.IMapper;
import sayit.server.contexts.*;
import sayit.server.db.common.IAccountHelper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The server class that handles all the requests.
 */
public class Server implements IServer {
    private final HttpServer _server;
    private final int _port;
    private final IEmailConfigurationHelper _emailDb;
    private final IAccountHelper _accountDb;
    private final IPromptHelper _promptDb;
    private final IWhisper _whisper;
    private final IChatGpt _chatgpt;
    private final IMapper<Message, MessagingException> _emailSender;


    /**
     * Creates a new server.
     *
     * @param configHelper  The email configuration helper to use.
     * @param pHelper       The prompt helper to use.
     * @param accountHelper The account helper to use.
     * @param emailSender   The email sender to use.
     * @param host          The host to use.
     * @param port          The port to use.
     * @param whisper       The <c>whisper</c> instance to use.
     * @param chatgpt       The <c>ChatGPT</c> instance to use.
     */
    private Server(IEmailConfigurationHelper configHelper,
                   IPromptHelper pHelper,
                   IAccountHelper accountHelper,
                   IMapper<Message, MessagingException> emailSender,
                   String host,
                   int port,
                   IWhisper whisper,
                   IChatGpt chatgpt) {
        this._port = port;
        this._emailDb = configHelper;
        this._accountDb = accountHelper;
        this._promptDb = pHelper;
        this._whisper = whisper;
        this._chatgpt = chatgpt;
        this._emailSender = emailSender;

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

        this._server.createContext("/input", new InputHandler(this));
        this._server.createContext("/history", new HistoryHandler(this));
        this._server.createContext("/delete-question", new DeleteHandler(this));
        this._server.createContext("/clear-all", new ClearAllHandler(this));

        this._server.createContext("/save_email_configuration", new SaveEmailConfigHandler(this));
        this._server.createContext("/get_email_configuration", new GetEmailConfigurationHandler(this));
        this._server.createContext("/send_email", new SendEmailHandler(this));

        this._server.createContext("/create-account", new CreateAccountHandler(this));
        this._server.createContext("/check-account", new CheckAccountExistHandler(this));
        this._server.createContext("/login", new LoginHandler(this));

        this._server.createContext("/ping", new PingHandler());
        this._server.setExecutor(threadPoolExecutor);
    }

    /**
     * Creates a new server builder.
     *
     * @return The server builder.
     */
    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

    /**
     * Starts the server.
     */
    public void start() {
        Thread serverThread = new Thread(this._server::start);
        serverThread.start();
        System.out.println("Server started on port " + this._port);
    }

    /**
     * Stops the server.
     */
    public void stop() {
        this._server.stop(0);
        System.out.println("Server stopped on port " + this._port);
    }

    /**
     * Gets the email sender.
     *
     * @return The email sender.
     */
    @Override
    public IEmailConfigurationHelper getEmailDb() {
        return this._emailDb;
    }

    /**
     * Gets the prompt helper.
     *
     * @return The prompt helper.
     */
    @Override
    public IPromptHelper getPromptDb() {
        return this._promptDb;
    }

    /**
     * Gets the account helper.
     *
     * @return The account helper.
     */
    @Override
    public IAccountHelper getAccountDb() {
        return this._accountDb;
    }

    /**
     * Gets the email sender.
     *
     * @return The email sender.
     */
    @Override
    public IChatGpt getChatGpt() {
        return this._chatgpt;
    }

    /**
     * Gets the email sender.
     *
     * @return The email sender.
     */
    @Override
    public IWhisper getWhisper() {
        return this._whisper;
    }

    /**
     * Gets the function that tries to send the specified message, and returns an exception if something bad
     * happened or null if it was successful.
     *
     * @return The function that tries to send the specified message.
     */
    @Override
    public IMapper<Message, MessagingException> getEmailSender() {
        return this._emailSender;
    }

    /**
     * A builder for the <c>Server</c> class.
     */
    public static class ServerBuilder {
        private IEmailConfigurationHelper _configHelper;
        private IPromptHelper _pHelper;
        private IAccountHelper _accountHelper;
        private String _host;
        private int _port;
        private IWhisper _whisper;
        private IChatGpt _chatgpt;
        private IMapper<Message, MessagingException> _emailSender;

        /**
         * Creates a new server builder with the default values.
         */
        public ServerBuilder() {
            this._host = ServerConstants.SERVER_HOSTNAME;
            this._port = ServerConstants.SERVER_PORT;
        }

        /**
         * Sets the email configuration helper to use.
         *
         * @param configHelper The email configuration helper to use.
         * @return The builder.
         */
        public ServerBuilder setEmailConfigurationHelper(IEmailConfigurationHelper configHelper) {
            this._configHelper = configHelper;
            return this;
        }

        /**
         * Sets the prompt helper to use.
         *
         * @param pHelper The prompt helper to use.
         * @return The builder.
         */
        public ServerBuilder setPromptHelper(IPromptHelper pHelper) {
            this._pHelper = pHelper;
            return this;
        }

        /**
         * Sets the account helper to use.
         *
         * @param accountHelper The account helper to use.
         * @return The builder.
         */
        public ServerBuilder setAccountHelper(IAccountHelper accountHelper) {
            this._accountHelper = accountHelper;
            return this;
        }

        /**
         * Sets the host to use.
         *
         * @param host The host to use.
         * @return The builder.
         */
        public ServerBuilder setHost(String host) {
            this._host = host;
            return this;
        }

        /**
         * Sets the port to use.
         *
         * @param port The port to use.
         * @return The builder.
         */
        public ServerBuilder setPort(int port) {
            this._port = port;
            return this;
        }

        /**
         * Sets the whisper instance to use.
         *
         * @param whisper The whisper instance to use.
         * @return The builder.
         */
        public ServerBuilder setWhisper(IWhisper whisper) {
            this._whisper = whisper;
            return this;
        }

        /**
         * Sets the chatgpt instance to use.
         *
         * @param chatgpt The chatgpt instance to use.
         * @return The builder.
         */
        public ServerBuilder setChatGpt(IChatGpt chatgpt) {
            this._chatgpt = chatgpt;
            return this;
        }

        /**
         * Sets the email sender to use.
         *
         * @param emailSender A function that takes a message and sends it, and returns whether it was successful.
         * @return The builder.
         */
        public ServerBuilder setEmailSender(IMapper<Message, MessagingException> emailSender) {
            this._emailSender = emailSender;
            return this;
        }

        /**
         * Builds the server.
         *
         * @return The server.
         */
        public Server build() {
            if (this._port == -1) {
                this._port = ServerConstants.SERVER_PORT;
            }

            if (this._host == null) {
                this._host = ServerConstants.SERVER_HOSTNAME;
            }

            return new Server(
                    this._configHelper,
                    this._pHelper,
                    this._accountHelper,
                    this._emailSender,
                    this._host,
                    this._port,
                    this._whisper,
                    this._chatgpt
            );
        }
    }
}
