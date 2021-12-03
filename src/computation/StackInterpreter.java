package computation;

import util.ListStack;
import util.StringUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;

public class StackInterpreter {
    private static final String EMPTY = "";
    private final ListStack<BigDecimal> stack = new ListStack<>();
    private final ListStack<Integer> routineStack = new ListStack<>();
    private final String[] program;
    private final HashMap<String, Integer> labels = new HashMap<>();
    int programCounter;

    public StackInterpreter(String[] program) {
        this.program = program;
        checkSyntax();
        programCounter = labels.get("main");
    }

    private void checkSyntax() {
        int i = 0;
        for (String line : program) {
            i++;
            if (line.length() == 0) // Empty line
                continue;

            if (line.charAt(0) == '#') // Comment
                continue;

            String[] tokens = StringUtil.tokenize(line);

            if (line.charAt(0) == ':') { // Label
                if (labels.put(tokens[0].substring(1), i - 1) != null)
                    throw new IllegalArgumentException("[SYNTAX] Duplicate label: " + line.substring(1)
                            + " (Line: " + i + ")");
                continue;
            }

            Mnemonic mnemonic;
            try {
                mnemonic = Mnemonic.parseMnemonic(tokens[0]);

            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException("[SYNTAX]" +
                        "Mnemonic \"" + tokens[0] + "\" is not known! (Line: " + i + ")");
            }

            switch (mnemonic) {
                case PUSH -> {
                    if (tokens.length < 2)
                        throw new IllegalArgumentException("[SYNTAX] Missing argument! (Line: " + i + ")");
                    new BigDecimal(tokens[1]);
                }
                case CALL, BEQ, BNEQ, BGT, BGE, BLT, BLE, BEZ, BNEZ, JMP -> {
                    if (tokens.length < 2)
                        throw new IllegalArgumentException("[SYNTAX] Missing argument! (Line: " + i + ")");
                    String raw = tokens[1].substring(1);
                    if (tokens[1].charAt(0) == '=') {
                        try {
                            if (Integer.parseInt(raw) >= program.length)
                                throw new IllegalArgumentException("[SYNTAX]" +
                                        " \"" + raw + "\" would be out of bounce! (Line: " + i + ")");
                            System.out.println("[WARNING] (conditional) jump based on line number! (Line: " + i + ")");
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("[SYNTAX]" +
                                    " \"" + raw + "\" is not a valid line number! (Line: " + i + ")");
                        }
                        continue;
                    }
                    if (tokens[1].charAt(0) != '>')
                        throw new IllegalArgumentException("[SYNTAX]"
                                + " Cannot jump to \"" + tokens[1] + "\"! Missing identifier '=' or '>'. (Line: " + i + ")");
                }
            }
        }
        System.gc();
    }

    public void execute() {
        while (true) {
            String line = EMPTY;
            try {
                line = program[(programCounter++)];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("[SYNTAX] missing RET statement as last statement! Program runs out of bounce. (Line: "
                        + program.length + ")");
                dumpStack();
                System.exit(1);
            }

            if (line.length() == 0)
                continue;

            String[] tokens = StringUtil.tokenize(line);

            if (tokens[0].charAt(0) == ':') // Label
                continue;
            if (tokens[0].charAt(0) == '#') // Comment
                continue;

            try {
                if (!action(tokens))
                    break;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("[FATAL] Error in line " + programCounter);
            }
        }
        dumpStack();
    }

    private void dumpStack() {
        ArrayList<BigDecimal> remaining = new ArrayList<>();
        while (stack.peek() != null)
            remaining.add(stack.pop());

        System.out.println("Remaining stack elements on finish: TOP -> " + remaining);
    }

    @SuppressWarnings("ConstantConditions") // "Argument 'arg' might be null"
    private boolean action(String[] tokens) {
        Mnemonic mnemonic = Mnemonic.parseMnemonic(tokens[0]);

        String arg = null;
        if (tokens.length > 1)
            arg = tokens[1];

        switch (mnemonic) {

            // Stack operations

            case PUSH -> stack.push(new BigDecimal(arg));
            case PEEK -> {
                if (arg == null)
                    System.out.println("PEEK: " + stack.peek());
                else
                    System.out.println("PEEK [" + arg + "]: " + stack.peek());
            }
            case POP -> {
                if (arg == null)
                    System.out.println("POP: " + stack.pop());
                else
                    System.out.println("POP [" + arg + "]: " + stack.pop());
            }
            case DUP -> stack.push(stack.peek());
            case SWAP -> {
                BigDecimal top = stack.pop();
                BigDecimal lower = stack.pop();
                stack.push(top);
                stack.push(lower);
            }
            case DROP -> stack.pop();
            case CALL -> {
                routineStack.push(programCounter);
                updateProgramCounter(arg);
            }
            case RET -> {
                Integer line = routineStack.pop();
                if (line == null)
                    return false;
                programCounter = line;
            }

            // Arithmetic operations

            case ADD -> stack.push(stack.pop().add(stack.pop()));
            case SUB -> {
                BigDecimal subtrahend = stack.pop();
                BigDecimal minuend = stack.pop();
                stack.push(minuend.subtract(subtrahend));
            }
            case SUM -> {
                BigDecimal sum = BigDecimal.ZERO;
                BigDecimal act;
                while ((act = stack.pop()) != null) {
                    sum = sum.add(act);
                }
                stack.push(sum);
            }
            case MUL -> stack.push(stack.pop().multiply(stack.pop()));
            case PROD -> {
                BigDecimal product = BigDecimal.ONE;
                BigDecimal act;
                while ((act = stack.pop()) != null) {
                    product = product.multiply(act);
                }
                stack.push(product);
            }
            case DIV -> {
                BigDecimal divisor = stack.pop();
                BigDecimal dividend = stack.pop();
                stack.push(dividend.divide(divisor, MathContext.DECIMAL128));
            }
            case MOD -> {
                BigDecimal top = stack.pop();
                BigDecimal lower = stack.pop();
                stack.push(new BigDecimal((lower.toBigIntegerExact().mod(top.toBigIntegerExact()))));
            }
            case SQRT -> stack.push(stack.pop().sqrt(MathContext.DECIMAL128));

            // Jump operations

            case BEQ -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) == 0)
                    updateProgramCounter(arg);
            }
            case BNEQ -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) != 0)
                    updateProgramCounter(arg);
            }
            case BGT -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) > 0)
                    updateProgramCounter(arg);
            }
            case BGE -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) >= 0)
                    updateProgramCounter(arg);
            }
            case BLT -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) < 0)
                    updateProgramCounter(arg);
            }
            case BLE -> {
                BigDecimal top = stack.pop();
                if (stack.pop().compareTo(top) <= 0)
                    updateProgramCounter(arg);
            }
            case BEZ -> {
                if (stack.pop().compareTo(BigDecimal.ZERO) == 0)
                    updateProgramCounter(arg);
            }
            case BNEZ -> {
                if (stack.pop().compareTo(BigDecimal.ZERO) != 0)
                    updateProgramCounter(arg);
            }
            case JMP -> updateProgramCounter(arg);
        }

        return true;
    }

    private void updateProgramCounter(String arg) {
        if (arg.charAt(0) == '=') {
            programCounter = Integer.parseInt(arg.substring(1)) - 1;
            return;
        }

        // arg.charAt(0) == '>'
        programCounter = labels.get(arg.substring(1));
    }
}
