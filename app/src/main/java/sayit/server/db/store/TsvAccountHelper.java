package sayit.server.db.store;

import sayit.server.db.common.IAccountHelper;
import sayit.server.db.doctypes.SayItAccount;

import java.util.List;

/**
 * A class containing several helper methods to interact with the <c>accounts</c>
 * TSV file.
 */
public class TsvAccountHelper implements IAccountHelper {
    private static final String ACCOUNTS_FILE = "accounts.tsv";

    private final TsvWriter<SayItAccount> _writer;

    /**
     * Create a new <c>TsvAccountHelper</c> instance.
     */
    public TsvAccountHelper() {
        this._writer = TsvWriter.createWriter(
                List.of(SayItAccount.USERNAME_FIELD, SayItAccount.PASSWORD_FIELD),
                ACCOUNTS_FILE,
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

    @Override
    public SayItAccount getAccount(String username) {
        return this._writer.getEntries().stream()
                .filter(account -> account.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createAccount(SayItAccount account) {
        this._writer.addEntry(account);
    }

    @Override
    public void save() {
        this._writer.save();
    }
}
