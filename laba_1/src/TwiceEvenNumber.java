import java.util.Scanner;

public class TwiceEvenNumber {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Введите трехзначное число: ");
        int number = scanner.nextInt();

        if (number < 100 || number > 999) {
            System.out.println("Ошибка: введено не трехзначное число.");
            return;
        }

        if (isTwiceEven(number)) {
            System.out.println("Число " + number + " является дважды четным.");
        } else {
            System.out.println("Число " + number + " не является дважды четным.");
        }
    }

    public static boolean isTwiceEven(int number) {
        int hundreds = number / 100;
        int tens = (number / 10) % 10;
        int units = number % 10;

        int sum = hundreds + tens + units;
        int product = hundreds * tens * units;

        return sum % 2 == 0 && product % 2 == 0;
    }
}
