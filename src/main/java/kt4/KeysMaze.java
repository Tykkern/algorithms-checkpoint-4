package kt4;

/**
 * Задача 5. Кратчайший путь по лабиринту с ключами и дверями.
 */
public final class KeysMaze {
    private static final int KEY_TYPES = 10;
    private static final int MASKS = 1 << KEY_TYPES;

    private KeysMaze() {
    }

    /**
     * Поиск в ширину, но состояние - это не только клетка, а пара
     * (клетка, набор собранных ключей). В одну и ту же клетку бывает выгодно
     * вернуться уже с ключом, поэтому посещённость отмечается для пары.
     * Набор ключей a..j хранится битовой маской из 10 бит, всего 1024 варианта.
     *
     * Дверь A..J можно пройти, только если в маске есть бит её ключа. Ключ
     * добавляется в маску при входе в его клетку. Состояние кодируется числом
     * cell * 1024 + mask. Состояний не больше 900 * 1024, примерно 920 тысяч,
     * каждое обрабатывается один раз: O(n * m * 2^k) по времени и памяти.
     *
     * @return минимальное число ходов или -1, если T недостижима
     */
    public static int shortestPath(String[] grid) {
        int n = grid.length;
        int m = grid[0].length();
        int start = GridPath.find(grid, 'S');
        int target = GridPath.find(grid, 'T');

        boolean[] visited = new boolean[n * m * MASKS];
        int[] queue = new int[n * m * MASKS];
        int head = 0;
        int tail = 0;
        queue[tail++] = start * MASKS;
        visited[start * MASKS] = true;

        // Обходим по уровням: все состояния одного уровня на одинаковом расстоянии от S.
        for (int steps = 0; head < tail; steps++) {
            int levelEnd = tail;
            while (head < levelEnd) {
                int state = queue[head++];
                int cell = state / MASKS;
                int keys = state % MASKS;
                if (cell == target)
                    return steps;

                int row = cell / m;
                int col = cell % m;
                for (int d = 0; d < 4; d++) {
                    int r = row + GridPath.DR[d];
                    int c = col + GridPath.DC[d];
                    if (r < 0 || r >= n || c < 0 || c >= m)
                        continue;

                    char symbol = grid[r].charAt(c);
                    if (symbol == '#')
                        continue;
                    if (symbol >= 'A' && symbol <= 'J' && (keys & (1 << (symbol - 'A'))) == 0)
                        continue;

                    int nextKeys = symbol >= 'a' && symbol <= 'j' ? keys | (1 << (symbol - 'a')) : keys;
                    int next = (r * m + c) * MASKS + nextKeys;
                    if (!visited[next]) {
                        visited[next] = true;
                        queue[tail++] = next;
                    }
                }
            }
        }
        return -1;
    }
}
