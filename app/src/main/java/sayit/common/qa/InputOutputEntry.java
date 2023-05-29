package sayit.common.qa;

/**
 * Represents a input, output.
 */
public class InputOutputEntry {
    private final int id;
    private final String type;
    private final UserInput userInput;
    private final ProgramOutput programOutput;

    /**
     * Creates a new <c>Entry</c> object with the specified input and output.
     *
     * @param i The input.
     * @param o The output.
     */
    public InputOutputEntry(UserInput i, ProgramOutput o) {
        this.id = -1;
        this.type = "";
        this.userInput = i;
        this.programOutput = o;
    }

    /**
     * Create a new <c>Entry</c> object with the specified id, type, input, and output.
     *
     * @param id The ID.
     * @param type The type of request by the user.
     * @param i The user input.
     * @param o The program output.
     */
    public InputOutputEntry(int id, String type, UserInput i, ProgramOutput o) {
        this.id = id;
        this.type = type;
        this.userInput = i;
        this.programOutput = o;
    }

    /**
     * Gets the question.
     *
     * @return The question.
     */
    public UserInput getInput() {
        return userInput;
    }

    /**
     * Gets the answer.
     *
     * @return The answer.
     */
    public ProgramOutput getOutput() {
        return programOutput;
    }

    /**
     * Gets the ID.
     *
     * @return The ID.
     */
    public int getID() {
        return this.id;
    }

    /**
     * Gets the Type.
     *
     * @return The Type.
     */
    public String getType() {
        return this.type;
    }

    /**
     * Checks if this <c>Entry</c> object is equal to another object.
     *
     * @param obj The object to compare to.
     * @return <c>true</c> if the objects are equal, <c>false</c> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InputOutputEntry o) {
            return this.userInput.equals(o.userInput) && this.programOutput.equals(o.programOutput)
                    && this.id == o.id && this.type.equals(o.type);
        }

        return false;
    }

    /**
     * Returns a string representation of this <c>InputOutputEntry</c> object.
     *
     * @return A string representation of this <c>InputOutputEntry</c> object.
     */
    @Override
    public String toString() {
        return "I: " + this.userInput + " / A: " + this.programOutput;
    }
}
