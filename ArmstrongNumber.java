import java.util.Scanner;
public class ArmstrongNumber {
    public static boolean isArmstrong(int num) {
        int original = num;
        int digits = countDigits(num);
        int sum = 0;

        while (num > 0) {
            int digit = num % 10;
            sum = sum + (int) Math.pow(digit, digits);
            num = num / 10;
        }

        return sum == original;
    }
    public static int countDigits(int num) {

        if (num == 0) {
            return 1;
        }

        int count = 0;

        while (num > 0) {
            count++;
            num = num / 10;
        }

        return count;
    }

    public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number: ");
        if (!sc.hasNextInt()) {
            System.out.println("Invalid input. Please enter a whole number.");
            sc.close();
            return;
        }
        int number = sc.nextInt();
        if (number < 0) {
            System.out.println("Please enter a non-negative number.");
        } 
        else if (isArmstrong(number)) {
            System.out.println(number + " is an Armstrong number.");
        } 
        else {
            System.out.println(number + " is not an Armstrong number.");
        }

        sc.close();
    }
}