package kt4;

/**
 * Задача 4. Наибольший прямоугольник из единиц в бинарной матрице.
 */
public final class MaxRectangle {

    private MaxRectangle() {
    }

    /**
     * Идём по строкам сверху вниз и для каждого столбца храним высоту столбика
     * из единиц, который заканчивается в текущей строке (heights). Любой
     * прямоугольник из единиц упирается нижней стороной в какую-то строку,
     * поэтому достаточно для каждой строки найти наибольший прямоугольник
     * в гистограмме heights.
     *
     * Гистограмма решается монотонным стеком за O(m), всего O(n * m) по времени
     * и O(m) по дополнительной памяти.
     *
     * @param rows строки матрицы из символов '0' и '1' одинаковой длины
     */
    public static int maxArea(String[] rows) {
        int m = rows.length == 0 ? 0 : rows[0].length();
        int[] heights = new int[m];
        int[] stack = new int[m + 1];
        int best = 0;

        for (String row : rows) {
            for (int col = 0; col < m; col++)
                heights[col] = row.charAt(col) == '1' ? heights[col] + 1 : 0;
            best = Math.max(best, largestInHistogram(heights, stack));
        }
        return best;
    }

    /**
     * В стеке индексы столбиков с возрастающей высотой. Когда приходит столбик
     * не выше вершины, для снимаемого столбика найдены обе границы: справа текущий
     * индекс i, слева новый элемент на вершине стека. Значит, прямоугольник
     * высотой этого столбика тянется от left + 1 до i - 1. В конце добавляем
     * фиктивный столбик высоты 0, чтобы снять всё, что осталось в стеке.
     */
    private static int largestInHistogram(int[] heights, int[] stack) {
        int best = 0;
        int size = 0;

        for (int i = 0; i <= heights.length; i++) {
            int current = i < heights.length ? heights[i] : 0;
            while (size > 0 && heights[stack[size - 1]] >= current) {
                int height = heights[stack[--size]];
                int left = size > 0 ? stack[size - 1] : -1;
                best = Math.max(best, height * (i - left - 1));
            }
            stack[size++] = i;
        }
        return best;
    }
}
