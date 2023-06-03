package sayit.server.db.store;

import sayit.server.db.common.IEmailConfigurationHelper;
import sayit.server.db.doctypes.SayItEmailConfiguration;

import java.util.List;

/**
 * A class containing several helper methods to interact with the <c>email_configurations</c>
 * TSV file.
 */
public class TsvEmailConfigurationHelper implements IEmailConfigurationHelper {
    private final TsvWriter<SayItEmailConfiguration> _writer;

    /**
     * Create a new <c>TsvEmailConfigurationHelper</c> instance.
     *
     * @param fileName The name of the file to use.
     */
    public TsvEmailConfigurationHelper(String fileName) {
        this._writer = TsvWriter.createWriter(
                List.of(SayItEmailConfiguration.ACC_USERNAME_FIELD, SayItEmailConfiguration.FIRST_NAME_FIELD,
                SayItEmailConfiguration.LAST_NAME_FIELD, SayItEmailConfiguration.DISPLAY_NAME_FIELD,
                SayItEmailConfiguration.EMAIL_FIELD, SayItEmailConfiguration.EMAIL_PASSWORD_FIELD,
                SayItEmailConfiguration.SMTP_FIELD, SayItEmailConfiguration.TLS_FIELD),
                fileName,
                new ITsvStrategy<>() {
                    @Override
                    public SayItEmailConfiguration parse(String[] columns) {
                        return new SayItEmailConfiguration(columns[0], columns[1], columns[2], columns[3],
                                columns[4], columns[5], columns[6], columns[7]);
                    }

                    @Override
                    public String[] write(SayItEmailConfiguration obj) {
                        return new String[]{obj.getAccUsername(), obj.getFirstName(),
                        obj.getLastName(), obj.getDisplayName(), obj.getEmail(), obj.getEmailPassword(),
                        obj.getSmtp(), obj.getTls()};
                    }
                });
    }

    /**
     * Gets the email configuration by username.
     *
     * @param username The username to search for.
     * @return The email configuration, or null if none exists.
     */
    @Override
    public SayItEmailConfiguration getEmailConfiguration(String username) {
        return this._writer.getEntries().stream()
                .filter(account -> account.getAccUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserts a new <c>SayItEmailConfiguration</c> into the database.
     *
     * @param config The email configuration to create.
     */
    @Override
    public void createEmailConfiguration(SayItEmailConfiguration config) {
        this._writer.addEntry(config);
        this._writer.save();
    }

    /**
     * Deletes a <c>SayItEmailConfiguration</c> from the database.
     *
     * @param username The user whose email configuration will be deleted.
     * @return true if an email configuration was deleted, false otherwise
     */
    @Override
    public boolean deleteEmailConfiguration(String username) {
        if (this.getEmailConfiguration(username) == null) {
            return false;
        }
        this._writer.removeEntry(this.getEmailConfiguration(username));
        this._writer.save();
        return true;
    }

    /**
     * Replaces a <c>SayItEmailConfiguration</c> from the database.
     * This method is only called after <c>getEmailConfiguration()</c> returns true.
     * Calls <c>deleteEmailConfiguration()</c> then <c>createEmailConfiguration()</c>.
     *
     * @param config The email configuration to be inserted into the databse, replacing
     *               a pre-existing one.
     */
    @Override
    public void replaceEmailConfiguration(SayItEmailConfiguration config) {
        this.deleteEmailConfiguration(config.getAccUsername());
        this._writer.save();
        this.createEmailConfiguration(config);
        this._writer.save();
    }

    @Override
    public void save() {
        this._writer.save();
    }
}
