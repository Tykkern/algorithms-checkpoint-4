package kt4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Запуск решения по номеру задачи: входные данные читаются из stdin
 * в формате из задания, ответ печатается в stdout.
 *
 * Пример: gradlew run -q --args="2" < input.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Укажите номер задачи от 1 до 6, входные данные подаются в stdin.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        solve(Integer.parseInt(args[0]), new FastReader(System.in), out);
        out.flush();
    }

    static void solve(int task, FastReader in, PrintWriter out) throws IOException {
        switch (task) {
            case 1 -> {
                int[] answer = PriceWait.waitDays(in.nextInts(in.nextInt()));
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < answer.length; i++) {
                    if (i > 0)
                        line.append(' ');
                    line.append(answer[i]);
                }
                out.println(line);
            }
            case 2 -> out.println(GridPath.shortestPath(readGrid(in)));
            case 3 -> {
                int n = in.nextInt();
                int start = in.nextInt();
                out.println(CycleDetector.hasCycle(in.nextInts(n), start) ? "YES" : "NO");
            }
            case 4 -> out.println(MaxRectangle.maxArea(readGrid(in)));
            case 5 -> out.println(KeysMaze.shortestPath(readGrid(in)));
            case 6 -> {
                int n = in.nextInt();
                out.println(DuplicateFinder.find(in.nextInts(n + 1)));
            }
            default -> throw new IllegalArgumentException("Нет задачи с номером " + task + ", есть 1-6.");
        }
    }

    // Формат: n и m, затем n строк по m символов без пробелов.
    private static String[] readGrid(FastReader in) throws IOException {
        int n = in.nextInt();
        int m = in.nextInt();
        String[] rows = new String[n];
        for (int i = 0; i < n; i++) {
            rows[i] = in.next();
            if (rows[i].length() != m)
                throw new IOException("В строке " + (i + 1) + " ожидалось " + m + " символов, а их " + rows[i].length() + ".");
        }
        return rows;
    }
}
