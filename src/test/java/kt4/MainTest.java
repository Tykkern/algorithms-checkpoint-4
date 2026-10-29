package kt4;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка чтения входных данных и формата вывода для каждой задачи.
 */
class MainTest {

    private static String run(int task, String input) throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        FastReader in = new FastReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        Main.solve(task, in, out);
        out.flush();
        return output.toString().replace("\r\n", "\n");
    }

    @Test
    void task1() throws IOException {
        assertEquals("1 1 4 2 1 1 0 0\n", run(1, "8\n73 74 75 71 69 72 76 73\n"));
    }

    @Test
    void task2() throws IOException {
        assertEquals("5\n", run(2, "3 3\nS.#\n.#T\n...\n"));
        assertEquals("-1\n", run(2, "1 3\nS#T\n"));
    }

    @Test
    void task3() throws IOException {
        assertEquals("YES\n", run(3, "3 1\n2 3 1\n"));
        assertEquals("NO\n", run(3, "3 1\n2 3 -1\n"));
    }

    @Test
    void task4() throws IOException {
        assertEquals("6\n", run(4, "4 5\n10100\n10111\n11111\n10010\n"));
    }

    @Test
    void task5() throws IOException {
        assertEquals("5\n", run(5, "3 3\nS.a\n#A#\n.T.\n"));
        assertEquals("-1\n", run(5, "1 5\nS.A.T\n"));
    }

    @Test
    void task6() throws IOException {
        assertEquals("2\n", run(6, "4\n1 3 4 2 2\n"));
    }

    @Test
    void windowsLineEndings() throws IOException {
        assertEquals("5\n", run(2, "3 3\r\nS.#\r\n.#T\r\n...\r\n"));
    }

    @Test
    void wrongRowLengthThrows() {
        assertThrows(IOException.class, () -> run(2, "2 3\nS.T\n..\n"));
    }

    @Test
    void unknownTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> run(0, ""));
    }
}
