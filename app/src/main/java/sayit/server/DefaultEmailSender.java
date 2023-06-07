package sayit.server;

import sayit.common.IMapper;

import javax.mail.Message;
import javax.mail.Transport;

/**
 * The default email sender.
 */
public class DefaultEmailSender implements IMapper<Message, Boolean> {
    @Override
    public Boolean map(Message message) {
        try {
            Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
