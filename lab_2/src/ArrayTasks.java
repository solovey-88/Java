import java.util.Arrays;

public class ArrayTasks {

    // 1. Найти наибольшую подстроку без повторяющихся символов
    public static String longestUniqueSubstring(String s) {
        int maxLen = 0;
        int start = 0;

        for (int i = 0; i < s.length(); i++) {
            boolean[] seen = new boolean[256];

            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);

                if (seen[c]) {
                    break;
                }

                seen[c] = true;

                if (j - i + 1 > maxLen) {
                    maxLen = j - i + 1;
                    start = i;
                }
            }
        }
        return s.substring(start, start + maxLen);
    }

    // 2. Объединить два отсортированных массива
    public static int[] mergeSortedArrays(int[] arr1, int[] arr2) {
        int n1 = arr1.length;
        int n2 = arr2.length;
        int[] result = new int[n1 + n2];
        int i = 0, j = 0, k = 0;

        while (i < n1 && j < n2) {
            if (arr1[i] <= arr2[j]) {
                result[k++] = arr1[i++];
            } else {
                result[k++] = arr2[j++];
            }
        }

        while (i < n1) result[k++] = arr1[i++];

        while (j < n2) result[k++] = arr2[j++];

        return result;
    }

    // 3. Найти максимальную сумму подмассива
    public static int maxSubarraySum(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int currentSum = nums[0];
        int maxSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    // 4. Повернуть массив на 90 градусов по часовой стрелке
    public static int[][] rotateClockwise(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return new int[0][0];

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                rotated[i][j] = matrix[rows - 1 - j][i];
            }
        }

        return rotated;
    }

    // 5. Найти пару элементов с заданной суммой
    public static int[] findPairWithSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{nums[i], nums[j]};
                }
            }
        }
        return null;
    }

    // 6. Найти сумму всех элементов в двумерном массиве
    public static int sum2DArray(int[][] matrix) {
        int sum = 0;

        for (int[] row : matrix) {
            for (int num : row) {
                sum += num;
            }
        }

        return sum;
    }

    // 7. Найти максимальный элемент в каждой строке
    public static int[] maxInEachRow(int[][] matrix) {
        int[] result = new int[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length == 0) continue;

            int max = matrix[i][0];

            for (int num : matrix[i]) {
                if (num > max) max = num;
            }

            result[i] = max;
        }

        return result;
    }

    // 8. Повернуть двумерный массив на 90 градусов против часовой стрелки
    public static int[][] rotateCounterClockwise(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return new int[0][0];

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                rotated[i][j] = matrix[j][cols - 1 - i];
            }
        }

        return rotated;
    }

    public static void main(String[] args) {
        System.out.println("Задача 1: " + longestUniqueSubstring("abcabcbb"));

        int[] merged = mergeSortedArrays(new int[]{1, 3, 5}, new int[]{2, 4, 6});
        System.out.print("Задача 2: ");
        System.out.println(Arrays.toString(merged));

        System.out.println("Задача 3: " + maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));

        int[][] rotatedClockwise = rotateClockwise(new int[][]{
                {1, 2, 3},
                {4, 5, 6}
        });
        System.out.println("Задача 4: ");
        for (int[] row : rotatedClockwise) {
            System.out.println(Arrays.toString(row));
        }

        int[] pair = findPairWithSum(new int[]{2, 7, 11, 15}, 9);
        System.out.print("Задача 5: ");
        System.out.println(Arrays.toString(pair));

        System.out.println("Задача 6: " + sum2DArray(new int[][]{{1, 2}, {3, 4}}));

        int[] maxRow = maxInEachRow(new int[][]{
                {1, 2, 3},
                {4, 5, 6}
        });
        System.out.print("Задача 7: ");
        System.out.println(Arrays.toString(maxRow));

        int[][] rotatedCounter = rotateCounterClockwise(new int[][]{
                {1, 2, 3},
                {4, 5, 6}
        });
        System.out.println("Задача 8: ");
        for (int[] row : rotatedCounter) {
            System.out.println(Arrays.toString(row));
        }
    }
}