import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

// NOTE: BigInteger is used only for convenience in printing and converting byte[]'s
import java.math.BigInteger;

public class MultiplicationTest {
    private static boolean QUIET = false;

    public static void main(final String[] args) {
        checkIfQuiet(args);
        printIfLoud("Enter each in decimal");
        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String one, two;
        try {
            printIfLoud("First multiplicand:");
            one = input.readLine();
            printIfLoud("Second multiplicand:");
            two = input.readLine();
        } catch (final IOException e) {
            System.err.println("Failed to read input");
            return;
        }
        final HeftyInteger hiOne = new HeftyInteger(new BigInteger(one).toByteArray());
        final HeftyInteger hiTwo = new HeftyInteger(new BigInteger(two).toByteArray());
        final HeftyInteger result = hiOne.multiply(hiTwo);
        printIfLoud("Result:");
        //System.out.println("quotient: " + result[0]);
        //System.out.println("remainder: " + result[1]);
        printHeftyInteger(result);
    }

    private static void printIfLoud(final String s) {
        if (!QUIET) System.out.println(s);
    }

    private static void checkIfQuiet(final String[] args) {
        if (args.length >= 1 && args[0].equals("-q")) QUIET = true;
    }

    public static void printHeftyInteger(final HeftyInteger hi) {
        System.out.println(new BigInteger(hi.getVal()).toString());
    }

}
