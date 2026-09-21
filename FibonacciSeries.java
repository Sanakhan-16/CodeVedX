import java.util.Scanner;

public class FibonacciSeries {
    public static void fibonacci(int n) {
        int a = 0;
        int b = 1;

        for (int i = 1; i <= n; i++) {
            System.out.print(a + " ");

            int c = a + b;
            a = b;
            b = c;
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of terms: ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            sc.close();
            return;
        }
        int n = sc.nextInt();
        if (n <= 0) {
            System.out.println("Please enter a number greater than 0.");
        } else {
            System.out.println("Fibonacci Series:");
            fibonacci(n);
        }

        sc.close();
    }
}
