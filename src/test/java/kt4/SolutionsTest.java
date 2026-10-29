package kt4;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SolutionsTest {

    private static final Random RANDOM = new Random(1);

    // Случайная карта из '.' и '#' с клетками S и T в разных местах.
    private static String[] randomMap(int n, int m, double wallChance) {
        char[][] cells = new char[n][m];
        for (char[] row : cells)
            for (int c = 0; c < m; c++)
                row[c] = RANDOM.nextDouble() < wallChance ? '#' : '.';

        int start = RANDOM.nextInt(n * m);
        int target;
        do {
            target = RANDOM.nextInt(n * m);
        } while (target == start);
        cells[start / m][start % m] = 'S';
        cells[target / m][target % m] = 'T';

        String[] rows = new String[n];
        for (int r = 0; r < n; r++)
            rows[r] = new String(cells[r]);
        return rows;
    }

    @Nested
    class PriceWaitTest {

        private int[] bruteForce(int[] prices) {
            int[] answer = new int[prices.length];
            for (int i = 0; i < prices.length; i++)
                for (int j = i + 1; j < prices.length; j++)
                    if (prices[j] > prices[i]) {
                        answer[i] = j - i;
                        break;
                    }
            return answer;
        }

        @Test
        void basicCase() {
            assertArrayEquals(new int[] {1, 1, 4, 2, 1, 1, 0, 0},
                    PriceWait.waitDays(new int[] {73, 74, 75, 71, 69, 72, 76, 73}));
        }

        @Test
        void equalPriceIsNotHigher() {
            assertArrayEquals(new int[] {0, 0, 0}, PriceWait.waitDays(new int[] {5, 5, 5}));
            assertArrayEquals(new int[] {2, 1, 0}, PriceWait.waitDays(new int[] {5, 5, 6}));
        }

        @Test
        void singleDayAndDecreasingPrices() {
            assertArrayEquals(new int[] {0}, PriceWait.waitDays(new int[] {7}));
            assertArrayEquals(new int[] {0, 0, 0}, PriceWait.waitDays(new int[] {3, 2, 1}));
        }

        @Test
        void randomPricesMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] prices = RANDOM.ints(1 + RANDOM.nextInt(30), 1, 10).toArray();
                assertArrayEquals(bruteForce(prices), PriceWait.waitDays(prices));
            }
        }

        @Test
        @Timeout(5)
        void largeDecreasingThenPeak() {
            int n = 200_000;
            int[] prices = new int[n];
            for (int i = 0; i < n - 1; i++)
                prices[i] = n - i;
            prices[n - 1] = 1_000_000_000;

            int[] answer = PriceWait.waitDays(prices);
            for (int i = 0; i < n - 1; i++)
                assertEquals(n - 1 - i, answer[i]);
            assertEquals(0, answer[n - 1]);
        }
    }

    @Nested
    class GridPathTest {

        @Test
        void straightLine() {
            assertEquals(2, GridPath.shortestPath(new String[] {"S.T"}));
            assertEquals(1, GridPath.shortestPath(new String[] {"S", "T"}));
        }

        @Test
        void detourAroundWalls() {
            assertEquals(4, GridPath.shortestPath(new String[] {
                    "S#.",
                    ".#.",
                    "..T",
            }));
            assertEquals(7, GridPath.shortestPath(new String[] {
                    "S.#T",
                    "#.#.",
                    "....",
            }));
        }

        @Test
        void unreachableTarget() {
            assertEquals(-1, GridPath.shortestPath(new String[] {"S#T"}));
            assertEquals(-1, GridPath.shortestPath(new String[] {
                    "S.#",
                    "##.",
                    "..T",
            }));
        }

        @Test
        void randomMapsMatchKeysMazeWithoutKeys() {
            for (int test = 0; test < 300; test++) {
                String[] grid = randomMap(1 + RANDOM.nextInt(8), 2 + RANDOM.nextInt(8), 0.3);
                assertEquals(KeysMaze.shortestPath(grid), GridPath.shortestPath(grid));
            }
        }

        @Test
        @Timeout(5)
        void maximumOpenMap() {
            String[] grid = new String[1000];
            Arrays.fill(grid, ".".repeat(1000));
            grid[0] = "S" + grid[0].substring(1);
            grid[999] = grid[999].substring(0, 999) + "T";
            assertEquals(1998, GridPath.shortestPath(grid));
        }
    }

    @Nested
    class CycleDetectorTest {

        // Проверка с массивом посещённых узлов, память O(n).
        private boolean bruteForce(int[] next, int start) {
            boolean[] visited = new boolean[next.length + 1];
            for (int node = start; node >= 1; node = next[node - 1]) {
                if (visited[node])
                    return true;
                visited[node] = true;
            }
            return false;
        }

        @Test
        void chainWithoutCycle() {
            assertFalse(CycleDetector.hasCycle(new int[] {2, 3, -1}, 1));
            assertFalse(CycleDetector.hasCycle(new int[] {-1}, 1));
        }

        @Test
        void cycleThroughStart() {
            assertTrue(CycleDetector.hasCycle(new int[] {2, 3, 1}, 1));
        }

        @Test
        void selfLoop() {
            assertTrue(CycleDetector.hasCycle(new int[] {1}, 1));
            assertTrue(CycleDetector.hasCycle(new int[] {2, 2}, 1));
        }

        @Test
        void tailLeadingIntoCycle() {
            assertTrue(CycleDetector.hasCycle(new int[] {2, 3, 4, 2}, 1));
        }

        @Test
        void cycleNotReachableFromStart() {
            int[] next = {2, -1, 4, 3};
            assertFalse(CycleDetector.hasCycle(next, 1));
            assertTrue(CycleDetector.hasCycle(next, 3));
        }

        @Test
        void randomGraphsMatchBruteForce() {
            for (int test = 0; test < 1000; test++) {
                int n = 1 + RANDOM.nextInt(12);
                int[] next = new int[n];
                for (int i = 0; i < n; i++)
                    next[i] = RANDOM.nextInt(4) == 0 ? -1 : 1 + RANDOM.nextInt(n);
                int start = 1 + RANDOM.nextInt(n);
                assertEquals(bruteForce(next, start), CycleDetector.hasCycle(next, start));
            }
        }

        @Test
        @Timeout(5)
        void longChains() {
            int n = 1_000_000;
            int[] next = new int[n];
            for (int i = 0; i < n - 1; i++)
                next[i] = i + 2;
            next[n - 1] = -1;
            assertFalse(CycleDetector.hasCycle(next, 1));

            next[n - 1] = n / 2;
            assertTrue(CycleDetector.hasCycle(next, 1));
        }
    }

    @Nested
    class MaxRectangleTest {

        private int bruteForce(String[] rows) {
            int n = rows.length;
            int m = rows[0].length();
            int best = 0;
            for (int top = 0; top < n; top++)
                for (int left = 0; left < m; left++)
                    for (int bottom = top; bottom < n; bottom++)
                        for (int right = left; right < m; right++) {
                            boolean allOnes = true;
                            for (int r = top; r <= bottom && allOnes; r++)
                                for (int c = left; c <= right && allOnes; c++)
                                    allOnes = rows[r].charAt(c) == '1';
                            if (allOnes)
                                best = Math.max(best, (bottom - top + 1) * (right - left + 1));
                        }
            return best;
        }

        @Test
        void basicCase() {
            assertEquals(6, MaxRectangle.maxArea(new String[] {
                    "10100",
                    "10111",
                    "11111",
                    "10010",
            }));
        }

        @Test
        void allZerosAndAllOnes() {
            assertEquals(0, MaxRectangle.maxArea(new String[] {"000", "000"}));
            assertEquals(12, MaxRectangle.maxArea(new String[] {"1111", "1111", "1111"}));
        }

        @Test
        void singleRowAndColumn() {
            assertEquals(3, MaxRectangle.maxArea(new String[] {"0110111"}));
            assertEquals(2, MaxRectangle.maxArea(new String[] {"1", "1", "0", "1"}));
            assertEquals(1, MaxRectangle.maxArea(new String[] {"1"}));
        }

        @Test
        void randomMatricesMatchBruteForce() {
            for (int test = 0; test < 300; test++) {
                int n = 1 + RANDOM.nextInt(6);
                int m = 1 + RANDOM.nextInt(6);
                String[] rows = new String[n];
                for (int r = 0; r < n; r++) {
                    StringBuilder row = new StringBuilder();
                    for (int c = 0; c < m; c++)
                        row.append(RANDOM.nextInt(4) == 0 ? '0' : '1');
                    rows[r] = row.toString();
                }
                assertEquals(bruteForce(rows), MaxRectangle.maxArea(rows));
            }
        }

        @Test
        @Timeout(5)
        void maximumSize() {
            String[] rows = new String[2000];
            Arrays.fill(rows, "1".repeat(1000));
            assertEquals(2_000_000, MaxRectangle.maxArea(rows));
        }
    }

    @Nested
    class KeysMazeTest {

        @Test
        void noKeysNoDoors() {
            assertEquals(2, KeysMaze.shortestPath(new String[] {"S.T"}));
        }

        @Test
        void doorWithoutKeyBlocksPath() {
            assertEquals(-1, KeysMaze.shortestPath(new String[] {"S.A.T"}));
        }

        @Test
        void keyBehindStartRequiresDetour() {
            // Сначала шаг влево за ключом, потом обратно и через дверь: 1 + 5 ходов.
            assertEquals(6, KeysMaze.shortestPath(new String[] {"aS.A.T"}));
        }

        @Test
        void returnToVisitedCellWithKey() {
            assertEquals(5, KeysMaze.shortestPath(new String[] {
                    "S.a",
                    "#A#",
                    ".T.",
            }));
        }

        @Test
        void keyLockedBehindItsOwnDoor() {
            assertEquals(-1, KeysMaze.shortestPath(new String[] {"S.Aa.T"}));
        }

        @Test
        void severalKeys() {
            assertEquals(5, KeysMaze.shortestPath(new String[] {"SbaBAT"}));
            assertEquals(-1, KeysMaze.shortestPath(new String[] {"SbBA.aT"}));
        }

        @Test
        void wrongKeyDoesNotOpenDoor() {
            assertEquals(-1, KeysMaze.shortestPath(new String[] {"SbAT"}));
        }

        @Test
        @Timeout(5)
        void maximumMapWithAllKeys() {
            // Ряд 15 - стена с дверью J у левого края, ключ j в правом верхнем углу.
            // Остальные ключи и двери разбросаны по карте и путь не удлиняют.
            char[][] cells = new char[30][30];
            for (char[] row : cells)
                Arrays.fill(row, '.');
            Arrays.fill(cells[15], '#');
            cells[15][0] = 'J';
            cells[0][29] = 'j';
            for (int i = 0; i < 9; i++) {
                cells[5][1 + i] = (char) ('a' + i);
                cells[25][1 + i] = (char) ('A' + i);
            }
            cells[0][0] = 'S';
            cells[29][29] = 'T';
            String[] grid = new String[30];
            for (int r = 0; r < 30; r++)
                grid[r] = new String(cells[r]);

            // 29 ходов до ключа, 44 до двери, 43 от двери до T.
            assertEquals(116, KeysMaze.shortestPath(grid));
        }
    }

    @Nested
    class DuplicateFinderTest {

        // Массив длины n + 1: значение duplicate встречается copies раз,
        // остальные места заняты разными числами из 1..n, порядок случайный.
        private int[] randomArray(int n, int duplicate, int copies) {
            List<Integer> others = new ArrayList<>();
            for (int value = 1; value <= n; value++)
                if (value != duplicate)
                    others.add(value);
            Collections.shuffle(others, RANDOM);

            List<Integer> values = new ArrayList<>(others.subList(0, n + 1 - copies));
            for (int i = 0; i < copies; i++)
                values.add(duplicate);
            Collections.shuffle(values, RANDOM);
            return values.stream().mapToInt(Integer::intValue).toArray();
        }

        @Test
        void basicCases() {
            assertEquals(2, DuplicateFinder.find(new int[] {1, 3, 4, 2, 2}));
            assertEquals(3, DuplicateFinder.find(new int[] {3, 1, 3, 4, 2}));
            assertEquals(1, DuplicateFinder.find(new int[] {1, 1}));
        }

        @Test
        void valueRepeatedManyTimes() {
            assertEquals(2, DuplicateFinder.find(new int[] {2, 2, 2, 2, 2}));
            assertEquals(4, DuplicateFinder.find(new int[] {1, 4, 4, 2, 4}));
        }

        @Test
        void arrayIsNotModified() {
            int[] nums = {3, 1, 3, 4, 2};
            DuplicateFinder.find(nums);
            assertArrayEquals(new int[] {3, 1, 3, 4, 2}, nums);
        }

        @Test
        void randomArrays() {
            for (int test = 0; test < 1000; test++) {
                int n = 1 + RANDOM.nextInt(30);
                int duplicate = 1 + RANDOM.nextInt(n);
                int copies = 2 + RANDOM.nextInt(n);
                assertEquals(duplicate, DuplicateFinder.find(randomArray(n, duplicate, copies)));
            }
        }

        @Test
        @Timeout(5)
        void maximumSize() {
            int n = 1_000_000;
            assertEquals(777_777, DuplicateFinder.find(randomArray(n, 777_777, 2)));
        }
    }
}
