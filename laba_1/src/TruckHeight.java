import java.util.Scanner;

public class TruckHeight {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите количество дорог: ");
        int numRoads = scanner.nextInt();

        int bestRoad = 0;
        int maxHeight = 0;

        for (int i = 1; i <= numRoads; i++) {
            System.out.print("Введите количество туннелей для дороги " + i + ": ");
            int numTunnels = scanner.nextInt();

            int minTunnelHeight = (int) Math.pow(10, 10);
            for (int j = 0; j < numTunnels; j++) {
                System.out.print("Введите высоту туннеля " + (j + 1) + " (в см): ");
                int tunnelHeight = scanner.nextInt();

                if (tunnelHeight < minTunnelHeight) {
                    minTunnelHeight = tunnelHeight;
                }
            }

            if (minTunnelHeight > maxHeight) {
                maxHeight = minTunnelHeight;
                bestRoad = i;
            }
        }

        System.out.println("Номер дороги: " + bestRoad);
        System.out.println("Максимальная высота грузовика: " + maxHeight + " см");
    }
}
