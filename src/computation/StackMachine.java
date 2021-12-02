package computation;

import util.ListStack;
import util.StringUtil;

import java.math.BigDecimal;
import java.math.MathContext;

public class StackMachine {
    private final ListStack<BigDecimal> stack = new ListStack<>();
    private final String[] program;
    int programCounter = 1;

    public StackMachine(String[] program) {
        this.program = program;
    }

    public void execute() {
        while (programCounter < program.length + 1) {
            String line = program[(programCounter++) - 1];
            if (line.length() == 0)
                continue;
            String[] tokens = StringUtil.tokenize(line);
            action(tokens);
        }

        int restElements = 0;
        while (stack.pop() != null)
            restElements++;

        System.out.println("Remaining stack elements on finish: " + restElements);
    }

    private void action(String[] args) {
        Mnemonic mnemonic;
        try {
            mnemonic = Mnemonic.parseMnemonic(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Mnemonic \"" + args[0] + "\" is not valid!");
            return;
        }

        BigDecimal arg = null;
        if (args.length > 1)
            arg = new BigDecimal(args[1]);

        switch (mnemonic) {
            case PUSH -> stack.push(arg);
            case PEEK -> System.out.println("PEEK: " + stack.peek());
            case POP -> System.out.println("POP: " + stack.pop());
            case DUP -> stack.push(stack.peek());
            case SWAP -> {
                BigDecimal top = stack.pop();
                BigDecimal lower = stack.pop();
                stack.push(top);
                stack.push(lower);
            }
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

            case BEQ -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) == 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case BNEQ -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) != 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case BEZ -> {
                if (stack.peek().compareTo(BigDecimal.ZERO) == 0)
                    programCounter = arg.toBigInteger().intValue();
            }
            case BNEZ -> {
                if (stack.peek().compareTo(BigDecimal.ZERO) != 0)
                    programCounter = arg.toBigInteger().intValue();
            }
            case BGT -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) > 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case BGE -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) >= 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case BLT -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) < 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case BLE -> {
                BigDecimal top = stack.pop();
                if (stack.peek().compareTo(top) <= 0)
                    programCounter = arg.toBigInteger().intValue();
            }

            case JMP -> programCounter = arg.toBigInteger().intValue();
        }
    }
}
