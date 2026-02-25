package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class FibonacciSourceTest {

    @Test
    void fibonacci() {
        // Base cases
        assertEquals(0, FibonacciSource.fibonacci(0));
        assertEquals(1, FibonacciSource.fibonacci(1));

        // Recursive cases
        assertEquals(1, FibonacciSource.fibonacci(2));
        assertEquals(2, FibonacciSource.fibonacci(3));
        assertEquals(3, FibonacciSource.fibonacci(4));
        assertEquals(5, FibonacciSource.fibonacci(5));
        assertEquals(55, FibonacciSource.fibonacci(10)); // Known correct value
    }

    @Test
    void testMainOutput() {
        // Capture console output
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(output));

        // Run main
        FibonacciSource.main(new String[]{});

        // Convert output to string
        String printed = output.toString().trim();

        // Expected output
        String expected = "The 10th term of the Fibonacci sequence is 55.";

        assertEquals(expected, printed);
    }
}
