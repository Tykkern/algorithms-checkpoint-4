package kt4;

/**
 * Задача 1. Через сколько дней цена станет выше.
 */
public final class PriceWait {

    private PriceWait() {
    }

    /**
     * Монотонный стек. В стеке лежат дни, для которых более высокая цена ещё
     * не встретилась; цены этих дней сверху вниз не убывают. Новый день
     * снимает со стека все дни с ценой строго ниже своей: для них он и есть
     * первый день с более высокой ценой. Дни, оставшиеся в стеке в конце,
     * так и не дождались, ответ для них 0.
     *
     * Каждый день кладётся в стек и снимается не больше одного раза:
     * O(n) по времени, O(n) по памяти. Стек сделан на массиве индексов.
     */
    public static int[] waitDays(int[] prices) {
        int n = prices.length;
        int[] answer = new int[n];
        int[] stack = new int[n];
        int size = 0;

        for (int day = 0; day < n; day++) {
            while (size > 0 && prices[stack[size - 1]] < prices[day]) {
                int earlier = stack[--size];
                answer[earlier] = day - earlier;
            }
            stack[size++] = day;
        }
        return answer;
    }
}
