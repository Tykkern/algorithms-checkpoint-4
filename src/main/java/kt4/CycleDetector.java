package kt4;

/**
 * Задача 3. Есть ли повторное посещение узла (цикл) на пути из start.
 */
public final class CycleDetector {
    private static final int END = -1;

    private CycleDetector() {
    }

    /**
     * Алгоритм Флойда, "черепаха и заяц". Два указателя идут из start:
     * медленный на один узел за шаг, быстрый на два. Если путь заканчивается,
     * быстрый первым дойдёт до конца, и цикла нет. Если путь зацикливается,
     * оба попадут в цикл, и быстрый догонит медленный: расстояние между ними
     * по циклу сокращается на 1 за шаг. Встреча означает повторный узел.
     *
     * Хранятся только два номера узла, поэтому дополнительная память O(1),
     * как требует задание. Время O(n): до встречи не больше длины хвоста
     * плюс длины цикла шагов.
     *
     * @param next  next[i - 1] - следующий узел для узла i (нумерация с 1),
     *              значение меньше 1 означает, что продолжения нет
     * @param start номер начального узла
     */
    public static boolean hasCycle(int[] next, int start) {
        int slow = start;
        int fast = start;

        while (true) {
            fast = step(next, fast);
            if (fast == END)
                return false;
            fast = step(next, fast);
            if (fast == END)
                return false;

            slow = step(next, slow);
            if (slow == fast)
                return true;
        }
    }

    private static int step(int[] next, int node) {
        int following = next[node - 1];
        return following >= 1 ? following : END;
    }
}
