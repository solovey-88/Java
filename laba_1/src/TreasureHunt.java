import java.util.Scanner;

public class TreasureHunt {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите координату клада по оси X: ");
        int treasureX = scanner.nextInt();
        System.out.print("Введите координату клада по оси Y: ");
        int treasureY = scanner.nextInt();

        int currentX = 0;
        int currentY = 0;

        int StepCount = 0;
        int FinalCount = 0;

        boolean treasureFound = false;

        while (true) {
            System.out.print("Введите направление (север, юг, запад, восток) или 'стоп': ");
            String direction = scanner.next();

            if (direction.equals("стоп")) {
                break;
            }

            System.out.print("Введите количество шагов: ");
            int steps = scanner.nextInt();

            StepCount++;

            switch (direction) {
                case "север":
                    currentY += steps;
                    break;
                case "юг":
                    currentY -= steps;
                    break;
                case "запад":
                    currentX -= steps;
                    break;
                case "восток":
                    currentX += steps;
                    break;
                default:
                    System.out.println("Неверное направление. Пропускаем указание.");
                    continue;
            }

            if (currentX == treasureX && currentY == treasureY) {
                treasureFound = true;
                FinalCount = StepCount;
            }
        }

        if (treasureFound) {
            System.out.println("Клад найден! Количество выполненных указаний: " + FinalCount);
        } else {
            System.out.println("Клад не найден. Количество выполненных указаний: " + StepCount);
        }
    }
}