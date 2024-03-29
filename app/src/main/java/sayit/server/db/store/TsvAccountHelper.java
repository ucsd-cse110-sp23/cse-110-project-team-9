package sayit.server.db.store;

import sayit.server.db.common.IAccountHelper;
import sayit.server.db.doctypes.SayItAccount;

import java.util.List;

/**
 * A class containing several helper methods to interact with the <c>accounts</c>
 * TSV file.
 */
public class TsvAccountHelper implements IAccountHelper {
    private final TsvWriter<SayItAccount> _writer;

    /**
     * Create a new <c>TsvAccountHelper</c> instance.
     *
     * @param fileName The name of the file to use.
     */
    public TsvAccountHelper(String fileName) {
        this._writer = TsvWriter.createWriter(
                List.of(SayItAccount.USERNAME_FIELD, SayItAccount.PASSWORD_FIELD),
                fileName,
                new ITsvStrategy<>() {
                    @Override
                    public SayItAccount parse(String[] columns) {
                        return new SayItAccount(columns[0], columns[1]);
                    }

                    @Override
                    public String[] write(SayItAccount obj) {
                        return new String[]{obj.getUsername(), obj.getPassword()};
                    }
                });
    }

    /**
     * Gets the account by username.
     *
     * @param username The username to search for.
     * @return The account, or null if none exists.
     */
    @Override
    public SayItAccount getAccount(String username) {
        return this._writer.getEntries().stream()
                .filter(account -> account.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * Inserts a new <c>SayItAccount</c> into the database.
     *
     * @param account The account to create.
     */
    @Override
    public void createAccount(SayItAccount account) {
        this._writer.addEntry(account);
    }

    @Override
    public void save() {
        this._writer.save();
    }
}
