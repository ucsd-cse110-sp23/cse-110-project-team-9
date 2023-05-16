package sayit;

import sayit.frontend.MainUserInterface;
import sayit.server.ServerConstants;
import sayit.server.Server;
import sayit.server.openai.ChatGpt;
import sayit.server.openai.Whisper;
import sayit.server.storage.TsvStore;

public class App {
    public static void main(String[] args) {
        new Thread(() -> {
            Server s = new Server(TsvStore.createOrOpenStore("data.tsv"),
                    ServerConstants.SERVER_HOSTNAME,
                    ServerConstants.SERVER_PORT,
                    new Whisper(ServerConstants.OPENAI_API_KEY),
                    new ChatGpt(ServerConstants.OPENAI_API_KEY));
            s.start();
        }).start();
        MainUserInterface.getInstance();
    }
}
