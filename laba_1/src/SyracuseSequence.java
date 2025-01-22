import java.util.Scanner;

public class SyracuseSequence {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите натуральное число: ");
        int n = scanner.nextInt();

        int steps = 0;
        while (n != 1) {
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = 3 * n + 1;
            }
            steps++;
        }

        System.out.println("Количество шагов: " + steps);
    }
}
