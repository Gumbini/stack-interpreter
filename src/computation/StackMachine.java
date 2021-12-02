package computation;

import util.ListStack;
import util.StringUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;

public class StackMachine {
    private final ListStack<BigDecimal> stack = new ListStack<>();
    private final String[] program;
    private final HashMap<String, Integer> labels = new HashMap<>();
    int programCounter = 0;

    public StackMachine(String[] program) {
        this.program = program;
        for (int i = 0; i < program.length; i++) {
            String line = program[i];
            if (line.length() == 0)
                continue;
            if (line.charAt(0) == ':') {
                if (labels.put(line.substring(1), i) != null)
                    throw new IllegalArgumentException("[SYNTAX] Duplicate label: " + line.substring(1)
                            + " (Line: " + (i + 1) + ")");
            }
        }
    }

    public void execute() {
        while (true) {
            String line;
            try {
                line = program[(programCounter++)];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new UnsupportedOperationException("[SYNTAX] missing RET statement! (Line: " + programCounter + ")");
            }
            if (line.length() == 0)
                continue;
            String[] tokens = StringUtil.tokenize(line);
            try {
                if (!action(tokens))
                    break;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("[SYNTAX] Error in line " + programCounter);
            }
        }

        ArrayList<BigDecimal> remaining = new ArrayList<>();
        while (stack.peek() != null)
            remaining.add(stack.pop());

        System.out.println("Remaining stack elements on finish: TOP -> " + remaining);
    }

    @SuppressWarnings("ConstantConditions") // "Argument 'arg' might be null"
    private boolean action(String[] args) {
        if (args[0].charAt(0) == ':') // Label
            return true;
        if (args[0].charAt(0) == '#') // Comment
            return true;

        Mnemonic mnemonic;
        try {
            mnemonic = Mnemonic.parseMnemonic(args[0]);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("[SYNTAX]" +
                    "Mnemonic \"" + args[0] + "\" is not known! (Line: " + programCounter + ")");
        }

        String arg = null;
        if (args.length > 1)
            arg = args[1];

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
            case RET -> {
                return false;
            }

            // Arithmetic operations

            case ADD -> stack.push(stack.pop().add(stack.pop()));
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
                BigDecimal top = stack.pop();
                BigDecimal lower = stack.pop();
                stack.push(lower.divide(top, MathContext.DECIMAL128));
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
            try {
                programCounter = Integer.parseInt(arg.substring(1)) - 1;
                System.out.println("[WARNING] (conditional) jump based on line number! (Line: " + programCounter + ")");
                return;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[SYNTAX]" +
                        " \"" + arg + "\" is not a valid line number! (Line: " + programCounter + ")");
            }
        }

        if (arg.charAt(0) == '>') {
            programCounter = labels.get(arg.substring(1));
            return;
        }

        throw new IllegalArgumentException("[SYNTAX]"
                + " Cannot jump to \"" + arg + "\" (Line: " + programCounter + ")");
    }
}
