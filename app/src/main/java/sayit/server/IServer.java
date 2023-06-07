package sayit.server;

import sayit.common.IMapper;
import sayit.server.db.common.IAccountHelper;
import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.common.IPromptHelper;
import sayit.server.openai.IChatGpt;
import sayit.server.openai.IWhisper;

import javax.mail.Message;

public interface IServer {
    /**
     * Gets the email configuration database.
     *
     * @return The email configuration database.
     */
    IEmailConfigurationHelper getEmailDb();

    /**
     * Gets the prompt database.
     *
     * @return The prompt database.
     */
    IPromptHelper getPromptDb();

    /**
     * Gets the account database.
     *
     * @return The account database.
     */
    IAccountHelper getAccountDb();

    /**
     * Gets the ChatGPT instance.
     *
     * @return The ChatGPT instance.
     */
    IChatGpt getChatGpt();

    /**
     * Gets the whisper instance.
     *
     * @return The whisper instance.
     */
    IWhisper getWhisper();

    /**
     * A function that takes a message and sends it, returning true if successful.
     *
     * @return A function that takes a message and sends it, returning true if successful.
     */
    IMapper<Message, Boolean> getEmailSender();
}
