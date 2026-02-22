//Declared package name
package org.example;
//Declares of functional imports
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//Declared Test Class
class FibonacciSourceTest {
    //Junit package for testing
    @org.junit.jupiter.api.Test
    //Testing for Fibonacci portion
    void fibonacci() {
        // Base cases
   assertEquals(0, FibonacciSource.fibonacci(0));
   assertEquals(1, FibonacciSource.fibonacci(1));
   // Recursive cases
        assertEquals(1, FibonacciSource.fibonacci(2));
        assertEquals(2, FibonacciSource.fibonacci(3));
        assertEquals(3, FibonacciSource.fibonacci(4));
        assertEquals(5, FibonacciSource.fibonacci(5));
        assertEquals(55, FibonacciSource.fibonacci(10)); // Known value
    }
    //A failed test tjt will show up as an error.
    //Testing for the main portion
    @org.junit.jupiter.api.Test
    void main() {
// Capture console output
java.io.ByteArrayOutputStream output = new
        java.io.ByteArrayOutputStream();
System.setOut(new java.io.PrintStream(output));
        // Run main
        FibonacciSource.main(new String[]{});
        // Convert output to string
        String printed = output.toString().trim();
        // Expected output
        String expected = "The 10th term of the Fibonacci sequence is 55."; assertEquals(expected, printed);
    }
}