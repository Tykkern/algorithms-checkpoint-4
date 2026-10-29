package kt4;

import java.util.Arrays;

/**
 * Задача 2. Кратчайший путь на карте от S до T.
 */
public final class GridPath {
    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    private GridPath() {
    }

    /**
     * Поиск в ширину из S. Все ходы стоят одинаково, поэтому клетки достаются
     * из очереди в порядке расстояния, и первое попадание в клетку даёт
     * кратчайший путь до неё. Клетка кодируется числом row * m + col,
     * очередь и расстояния лежат в массивах размера n * m.
     * Каждая клетка обрабатывается один раз: O(n * m) по времени и памяти.
     *
     * @return минимальное число ходов или -1, если T недостижима
     */
    public static int shortestPath(String[] grid) {
        int n = grid.length;
        int m = grid[0].length();
        int start = find(grid, 'S');
        int target = find(grid, 'T');

        int[] distance = new int[n * m];
        Arrays.fill(distance, -1);
        int[] queue = new int[n * m];
        int head = 0;
        int tail = 0;
        queue[tail++] = start;
        distance[start] = 0;

        while (head < tail) {
            int cell = queue[head++];
            if (cell == target)
                return distance[cell];

            int row = cell / m;
            int col = cell % m;
            for (int d = 0; d < 4; d++) {
                int r = row + DR[d];
                int c = col + DC[d];
                if (r < 0 || r >= n || c < 0 || c >= m || grid[r].charAt(c) == '#')
                    continue;

                int next = r * m + c;
                if (distance[next] == -1) {
                    distance[next] = distance[cell] + 1;
                    queue[tail++] = next;
                }
            }
        }
        return -1;
    }

    // Номер клетки с символом symbol в виде row * m + col.
    static int find(String[] grid, char symbol) {
        int m = grid[0].length();
        for (int row = 0; row < grid.length; row++) {
            int col = grid[row].indexOf(symbol);
            if (col >= 0)
                return row * m + col;
        }
        throw new IllegalArgumentException("На карте нет клетки " + symbol + ".");
    }
}
