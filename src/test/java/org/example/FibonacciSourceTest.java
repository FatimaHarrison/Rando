package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

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
        assertEquals(55, FibonacciSource.fibonacci(10));
    }

    @Test
    void testMainOutput() {
        // Save original System.out
        PrintStream originalOut = System.out;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        // Run main
        FibonacciSource.main(new String[]{});

        // Restore System.out
        System.setOut(originalOut);

        String printed = output.toString().trim();
        String expected = "The 10th term of the Fibonacci sequence is 55.";

        assertEquals(expected, printed);
    }

    @Test
    @org.junit.jupiter.api.Timeout(2)
    void fibonacciTimeout() {
        assertEquals(55, FibonacciSource.fibonacci(10));
    }
}
