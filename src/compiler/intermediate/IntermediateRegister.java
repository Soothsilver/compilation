package compiler.intermediate;

public class IntermediateRegister {
    int index;

    public IntermediateRegister(int index) {
        this.index = index;
    }

    public String mipsAcquireValueFromRegister(String incomingValueRegister) {
        return ""; // Let's just... not acquire the value.
    }
}
