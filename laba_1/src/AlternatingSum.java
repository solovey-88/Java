import java.util.Scanner;

public class AlternatingSum {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите количество чисел: ");
        int n = scanner.nextInt();

        int[] numbers = new int[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Введите число " + (i + 1) + ": ");
            numbers[i] = scanner.nextInt();
        }

        int result = alternatingSum(numbers);

        System.out.println("Знакочередующаяся сумма: " + result);
    }

    public static int alternatingSum(int[] numbers) {
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (i % 2 == 0) {
                sum += numbers[i];
            } else {
                sum -= numbers[i];
            }
        }
        return sum;
    }
}
