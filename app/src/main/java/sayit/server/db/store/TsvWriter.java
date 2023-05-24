package sayit.server.db.store;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TsvWriter<T> {
    private static final String DELIMITER = "\t";
    private static final String NEW_LINE = "\n";
    private static final String SERIALIZED_NEW_LINE = "~g~r~e~g~m~i~r~a~n~d~a~";

    private final String _columnHeader;
    private final String _fileName;
    private final ITsvStrategy<T> _strategy;
    private final List<T> _entries;

    private TsvWriter(List<String> columnNames, String filePath, ITsvStrategy<T> strategy) throws IOException {
        this._columnHeader = String.join(DELIMITER, columnNames);
        this._fileName = filePath;
        this._strategy = strategy;
        this._entries = new ArrayList<>();

        File file = new File(this._fileName);
        // If the file exists, read the contents and set the id to the last id in the file.
        if (file.exists()) {
            try (FileInputStream stream = new FileInputStream(file);
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(reader)) {
                List<String> lines = bufferedReader.lines().toList();
                // Skip the first line (i = 0) since that's the TSV header.
                for (int i = 1; i < lines.size(); ++i) {
                    String[] columns = lines.get(i).split(DELIMITER);
                    if (columns.length != columnNames.size()) {
                        continue;
                    }

                    for (int j = 0; j < columns.length; ++j) {
                        columns[j] = columns[j].replaceAll(SERIALIZED_NEW_LINE, NEW_LINE);
                    }

                    this._entries.add(this._strategy.parse(columns));
                }
            }
        } else if (file.createNewFile()) {
            // If the file doesn't exist, create it and write the header.
            Files.write(file.toPath(), this._columnHeader.getBytes());
        } else {
            throw new IOException("Unable to create file " + this._fileName);
        }
    }

    /**
     * Creates a new instance of the <c>TsvWriter</c> class.
     *
     * @param columnNames The names of the columns in the TSV file. This also defines the
     *                    number of columns in the TSV file.
     * @param filePath    The path to the TSV file.
     * @param strategy    The strategy to use to read from and write to the TSV file.
     * @param <T>         The type of object to read from and write to the TSV file.
     * @return A new instance of the <c>TsvWriter</c> class, if created successfully.
     */
    public static <T> TsvWriter<T> createWriter(List<String> columnNames, String filePath, ITsvStrategy<T> strategy) {
        try {
            return new TsvWriter<>(columnNames, filePath, strategy);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Adds an entry to the TSV file.
     *
     * @param entry The entry to add to the TSV file.
     */
    public void addEntry(T entry) {
        this._entries.add(entry);
    }

    /**
     * Removes an entry from the TSV file.
     *
     * @param entry The entry to remove from the TSV file.
     */
    public void removeEntry(T entry) {
        this._entries.remove(entry);
    }

    /**
     * Removes all entries from the TSV file that match the predicate.
     *
     * @param pred The predicate to use to determine which entries to remove.
     * @return The number of entries removed.
     */
    public int removeEntriesBy(Predicate<T> pred) {
        int count = 0;
        for (int i = this._entries.size() - 1; i >= 0; --i) {
            if (pred.test(this._entries.get(i))) {
                this._entries.remove(i);
                ++count;
            }
        }
        return count;
    }

    /**
     * Gets all entries in the TSV file.
     *
     * @return All entries in the TSV file.
     */
    public List<T> getEntries() {
        return ImmutableList.copyOf(this._entries);
    }

    /**
     * Saves the contents of the store to the file. This must be executed if you want to ensure that any changes
     * to the store are saved.
     *
     * @return <c>true</c> if the save was successful, <c>false</c> otherwise.
     */
    public boolean save() {
        try {
            File file = new File(this._fileName);
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(this._columnHeader + System.lineSeparator());
                for (var entry : this._entries) {
                    String[] columns = this._strategy.write(entry);
                    for (int i = 0; i < columns.length; ++i) {
                        columns[i] = columns[i].replaceAll(NEW_LINE, SERIALIZED_NEW_LINE);
                    }

                    writer.write(String.join(DELIMITER, columns) + System.lineSeparator());
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the number of entries in the TSV file.
     *
     * @return The number of entries in the TSV file.
     */
    public int size() {
        return this._entries.size();
    }

    /**
     * Clears all entries in the TSV file and deletes the file.
     *
     * @return <c>true</c> if the clear was successful, <c>false</c> otherwise.
     */
    public boolean clearAll() {
        this._entries.clear();
        File file = new File(this._fileName);
        return file.delete();
    }
}
