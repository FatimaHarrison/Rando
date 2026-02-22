package org.example;
public class FibonacciSource {
//The setting of the Fibonacci sequence.
    /**
     * Recursively computes the nth Fibonacci number.
     * <p>
     * The Fibonacci sequence is defined as:
     * <br>fib(0) = 0
     * <br>fib(1) = 1
     * <br>fib(n) = fib(n - 1) + fib(n - 2) for n >= 2
     *
     * @param n is the position in the Fibonacci sequence to compute
     * @return the nth Fibonacci number
     */
    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;  // Base cases
        }
        //Returning the recursive case
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
//The main function that calls the recursive Fibonacci method
    /* and prints out the results for n=10.
     */
    /**
     * The main method that calls the recursive Fibonacci method
     * and prints out the result for n=10.
     *
     * @param args command line arguments is not used.
     */
    public static void main(String[] args) {
        //Declaring the variables
        int n = 788; // Integer n = 10
        int result = fibonacci(n);  // Call the recursive method
        //The output of the sequence print out line
        System.out.println("The " + n +
                "th term Fatima's Fibonacci sequence is" + result + ".");

//Dramra of future amofhg uioiv eisghoighw h9wejbwbe g9wjeve wwevjihhv
        //giwjbiwgvwhguweogiwehguw
        //uigowliejgnjwegn ewguowjgw
        //giyuowjgiweg
        //jguywhgiw iughoweg bighworg w9gojwog bwigwog iwghowieg
    }
}
