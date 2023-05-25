package sayit;

import sayit.frontend.MainUserInterface;
import sayit.server.Server;
import sayit.server.ServerConstants;
import sayit.server.db.mongo.MongoAccountHelper;
import sayit.server.db.mongo.MongoWrapper;
import sayit.server.openai.ChatGpt;
import sayit.server.openai.Whisper;
import sayit.server.storage.TsvStore;

public class App {
    public static void main(String[] args) {
        new Thread(() -> {
            MongoWrapper mongo = MongoWrapper.getOrCreateInstance(ServerConstants.MONGO_URI);
            Server s = Server.builder()
                    .setHost(ServerConstants.SERVER_HOSTNAME)
                    .setPort(ServerConstants.SERVER_PORT)
                    .setWhisper(new Whisper(ServerConstants.OPENAI_API_KEY))
                    .setChatGpt(new ChatGpt(ServerConstants.OPENAI_API_KEY))
                    .setStorage(TsvStore.createOrOpenStore("data.tsv"))
                    .setAccountHelper(MongoAccountHelper.getOrCreateInstance(mongo))
                    .build();

            s.start();
        }).start();
        MainUserInterface.getInstance();
    }
}
