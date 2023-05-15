package sayit;

import sayit.frontend.MainUserInterface;
import sayit.server.Constants;
import sayit.server.Server;
import sayit.server.openai.ChatGpt;
import sayit.server.openai.Whisper;
import sayit.server.storage.TsvStore;

public class App {
    public static void main(String[] args) {
        new Thread(() -> {
            Server s = new Server(TsvStore.createOrOpenStore("data.tsv"),
                    Constants.SERVER_HOSTNAME,
                    Constants.SERVER_PORT,
                    new Whisper(Constants.OPENAI_API_KEY),
                    new ChatGpt(Constants.OPENAI_API_KEY, 100));
            s.start();
        }).start();
        MainUserInterface.getInstance();
    }
}
