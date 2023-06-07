package sayit.server;

import sayit.common.IMapper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * The default email sender.
 */
public class DefaultEmailSender implements IMapper<Message, MessagingException> {
    @Override
    public MessagingException map(Message message) {
        try {
            Transport.send(message);
            return null;
        } catch (MessagingException e) {
            return e;
        }
    }
}
