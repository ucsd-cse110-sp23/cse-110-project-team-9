package sayit.server.db.doctypes;

import org.bson.codecs.pojo.annotations.BsonProperty;
import sayit.common.UniversalConstants;

/**
 * A class representing an email configuration in the <c>sayit</c> database. Each
 * user only has one email configuration. Each configuration has a <c>username</c> field,
 * which is how we know which configurations belong to which user.
 */
public class SayItEmailConfiguration {

    //Document Fields
    public static final String ACC_USERNAME_FIELD = UniversalConstants.USERNAME;
    public static final String FIRST_NAME_FIELD = "first_name";
    public static final String LAST_NAME_FIELD = "last_name";
    public static final String DISPLAY_NAME_FIELD = "display_name";
    public static final String EMAIL_FIELD = "email";
    public static final String EMAIL_PASSWORD_FIELD = "email_password";
    public static final String SMTP_FIELD = "smtp";
    public static final String TLS_FIELD = "tls";

    @BsonProperty(ACC_USERNAME_FIELD)
    private String accUsername;

    @BsonProperty(FIRST_NAME_FIELD)
    private String firstName;

    @BsonProperty(LAST_NAME_FIELD)
    private String lastName;

    @BsonProperty(DISPLAY_NAME_FIELD)
    private String displayName;

    @BsonProperty(EMAIL_FIELD)
    private String email;

    @BsonProperty(EMAIL_PASSWORD_FIELD)
    private String emailPassword;

    @BsonProperty(SMTP_FIELD)
    private String smtp;

    @BsonProperty(TLS_FIELD)
    private String tls;

    /**
     * Create a new <c>SayItEmailConfiguration</c> with no properties.
     */
    public SayItEmailConfiguration() {
    }

    /**
     * Create a new <c>SayItEmailConfiguration</c> with the specified arguments.
     *
     * @param accUsername   The account username.
     * @param firstName     The first name.
     * @param lastName      The last name.
     * @param displayName   The display name for the email.
     * @param email         The email being used to send emails.
     * @param emailPassword The email password.
     * @param smtp          The smtp port.
     * @param tls           The tls port.
     */
    public SayItEmailConfiguration(String accUsername, String firstName, String lastName,
                                   String displayName, String email, String emailPassword,
                                   String smtp, String tls) {
        this.accUsername = accUsername;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.emailPassword = emailPassword;
        this.smtp = smtp;
        this.tls = tls;
    }

    /**
     * Gets the account username associated with this email configuration.
     *
     * @return The account username.
     */
    public String getAccUsername() {
        return accUsername;
    }

    /**
     * Gets the first name associated with this email configuration.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name associated with this email configuration.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the display name associated with this email configuration.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the email associated with this email configuration.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the email password associated with this email configuration.
     *
     * @return The email password.
     */
    public String getEmailPassword() {
        return emailPassword;
    }

    /**
     * Gets the smtp port associated with this email configuration.
     *
     * @return The smtp port.
     */
    public String getSmtp() {
        return smtp;
    }

    /**
     * Gets the tls port associated with this email configuration.
     *
     * @return The tls port.
     */
    public String getTls() {
        return tls;
    }

    /**
     * Sets the account username associated with this email configuration.
     *
     * @param accUsername The account username.
     */
    public void setAccUsername(String accUsername) {
        this.accUsername = accUsername;
    }

    /**
     * Sets the first name associated with this email configuration.
     *
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name associated with this email configuration.
     *
     * @param lastName The last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the display name associated with this email configuration.
     *
     * @param displayName The display name.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Sets the email associated with this email configuration.
     *
     * @param email The account email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the email password associated with this email configuration.
     *
     * @param emailPassword The email password.
     */
    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    /**
     * Sets the smtp port associated with this email configuration.
     *
     * @param smtp The smtp port.
     */
    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    /**
     * Sets the tls port associated with this email configuration.
     *
     * @param tls The tls port.
     */
    public void setTls(String tls) {
        this.tls = tls;
    }

    /**
     * Returns a string representation of this SayItEmailConfiguration.
     *
     * @return A string representation of this SayItEmailConfiguration.
     */
    @Override
    public String toString() {
        return "SayItEmailConfiguration{"
                + "accUsername'" + accUsername + '\''
                + ", /firstName'" + firstName + '\''
                + ", /lastName'" + lastName + '\''
                + ", /displayName'" + displayName + '\''
                + ", /email'" + email + '\''
                + ", /emailPassword'" + emailPassword + '\''
                + ", /smtp'" + smtp + '\''
                + ", /tls'" + tls + '\''
                + '}';
    }

    /**
     * Checks if the provided object (<c>SayItEmailConfiguration</c>) has the same fields
     * as this <c>SayItEmailConfiguration</c>.
     *
     * @param obj the object being compared to
     * @return true if the fields are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SayItEmailConfiguration config) {
            return (this.accUsername.equals(config.getAccUsername())
                    && this.firstName == config.getFirstName()
                    && this.lastName.equals(config.getLastName())
                    && this.displayName.equals(config.getDisplayName())
                    && this.email.equals(config.getEmail())
                    && this.emailPassword.equals(config.getEmailPassword())
                    && this.smtp.equals(config.getSmtp())
                    && this.tls.equals(config.getTls()));
        }
        return false;
    }
}
