import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class Main {
    private static final ListStack<BigDecimal> stack = new ListStack<>();

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            throw new IllegalArgumentException("Please specify a path! (e.g. \".\")");

        File argsFile = new File(args[0]);
        parseFile(argsFile);

        int restElements = 0;
        while (stack.pop() != null)
            restElements++;

        System.out.println("Remaining stack elements on exit: " + restElements);
    }

    private static void parseFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() == 0)
                continue;
            String[] tokens = StringUtil.tokenize(line);
            action(tokens);
        }
        reader.close();
    }

    private static void action(String[] args) {
        Mnemonic mnemonic;
        try {
            mnemonic = Mnemonic.parseMnemonic(args[0]);
        } catch (IllegalArgumentException e) {
            System.out.println("Mnemonic \"" + args[0] + "\" is not valid!");
            return;
        }

        switch (mnemonic) {
            case PUSH -> stack.push(new BigDecimal(args[1]));
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
            case SQRT -> stack.push(stack.pop().sqrt(MathContext.DECIMAL128));
        }
    }
}
