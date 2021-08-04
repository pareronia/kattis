package com.github.pareronia.kattis.hotspot;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Hot Spot
 * @see <a href="https://open.kattis.com/problems/hotspot">https://open.kattis.com/problems/hotspot</a>
 */
public class HotSpot {

    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final char RED = 'R';
    private static final char GREEN = 'G';
    private static final char BLUE = 'B';
    
    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public HotSpot(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    @SuppressWarnings("unused")
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    @SuppressWarnings("unused")
    private void printGrid(final char[][] grid) {
        Arrays.stream(grid).forEach(r ->
                log(() -> new String(r)));
    }
    
    private static final class State implements Comparable<State> {
        private final Board board;
        private final Integer jumps;
        
        private State(final Board board, final int jumps) {
            this.board = board;
            this.jumps = jumps;
        }

        public static State of(final Board board, final int jumps) {
            return new State(board, jumps);
        }

        @Override
        public int compareTo(final State other) {
            final int jumps = this.jumps.compareTo(other.jumps);
            if (jumps == 0) {
                return this.board.getRed().distanceFromHome()
                        .compareTo(other.board.getRed().distanceFromHome());
            }
            return jumps;
        }
    }
    
    private int bfs(final Board board) {
        final PriorityQueue<State> queue = new PriorityQueue<>();
        State state = State.of(board, 0);
        queue.add(state);
        final Set<Board> seen = new HashSet<>();
        seen.add(board);
        int cnt = 0;
        int maxSize = 0;
        while (!queue.isEmpty()) {
            state = queue.poll();
            maxSize = Math.max(maxSize, queue.size());
            if (state.board.getRed().distanceFromHome() == 0) {
                cnt = state.jumps;
                break;
            }
            for (final Move move : state.board.validMoves()) {
                final Board newBoard = state.board.doMove(move);
                if (!seen.contains(newBoard)) {
                    seen.add(newBoard);
                    queue.add(State.of(newBoard, state.jumps + 1));
                }
            }
        }
        final String maxSizeS = String.valueOf(maxSize);
        log(() -> maxSizeS);
        return cnt;
    }
    
    private Result<?> handleTestCase(final Integer i, final FastScanner sc) {
        final char [][] g = new char[ROWS][COLS];
        Robot RR = null;
        final Set<Robot> R = new HashSet<>();
        for (int r = 0; r < ROWS; r++) {
            final String row = sc.next();
            for (int c = 0; c < COLS; c++) {
                g[r][c] = row.charAt(c);
                if (g[r][c] == RED) {
                    RR = Robot.red(r, c);
                    R.add(Robot.red(r, c));
                } else if (g[r][c] == GREEN) {
                    R.add(Robot.green(r, c));
                } else if (g[r][c] == BLUE) {
                    R.add(Robot.blue(r, c));
                }
            }
        }
        printGrid(g);
        final Board board = Board.of(RR, R);
        final int ans = bfs(board);
        return new Result<>(i, List.of(ans));
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases;
            if (this.sample) {
                numberOfTestCases = sc.nextInt();
            } else {
                numberOfTestCases = 1;
            }
            final List<Result<?>> results
                    = Stream.iterate(1, i -> i <= numberOfTestCases, i -> i + 1)
                            .map(i -> handleTestCase(i, sc))
                            .collect(toList());
            output(results);
        }
    }

    private void output(final List<Result<?>> results) {
        results.forEach(r -> {
            r.getValues().stream().map(Object::toString).forEach(this.out::println);
        });
    }
    
    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = HotSpot.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new HotSpot(sample, is, out).solve();
        
        out.flush();
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "µs";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path
                    = Paths.get(HotSpot.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format(
                        "Expected %s, got %s", expected, actual));
            }
            actual.forEach(System.out::println);
            System.out.println(String.format("took: %.3f %s", time, unit));
        }
    }
    
    private static boolean isSample() {
        try {
            return "sample".equals(System.getProperty("kattis"));
        } catch (final SecurityException e) {
            return false;
        }
    }
    
    private static final class FastScanner implements Closeable {
        private final BufferedReader br;
        private StringTokenizer st;
        
        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
            st = new StringTokenizer("");
        }
        
        public String next() {
            while (!st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }
    
        public int nextInt() {
            return Integer.parseInt(next());
        }
        
        @SuppressWarnings("unused")
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
        }
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
        }

        @Override
        public void close() {
            try {
                this.br.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }
    
    private static final class Result<T> {
        @SuppressWarnings("unused")
        private final int number;
        private final List<T> values;
        
        public Result(final int number, final List<T> values) {
            this.number = number;
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
    }
    
    private static final class Board {
        private final Robot red;
        private final Set<Robot> robots;
        
        private Board(final Robot red, final Set<Robot> robots) {
            this.red = red;
            this.robots = robots;
        }

        public static Board of(final Robot red, final Set<Robot> robots) {
            return new Board(red, robots);
        }
        
        public Board doMove(final Move move) {
            final Set<Robot> newRobots = new HashSet<>(this.robots);
            newRobots.remove(move.getRobot());
            newRobots.add(move.getRobot().withPosition(move.getTo()));
            final Robot newRed = newRobots.stream()
                    .filter(Robot::isRed).findFirst().orElseThrow();
            return new Board(newRed, newRobots);
        }
        
        private Set<Robot> allRobots() {
            final Set<Robot> allRobots = new HashSet<>(this.robots);
            allRobots.add(red);
            return Collections.unmodifiableSet(allRobots);
        }
        
        public List<Move> validMoves() {
            return allRobots().stream()
                .flatMap(r -> Stream.of(
                        this.jumpUp(r),
                        this.jumpDown(r),
                        this.jumpLeft(r),
                        this.jumpRight(r),
                        this.doubleJumpUp(r),
                        this.doubleJumpDown(r),
                        this.doubleJumpLeft(r),
                        this.doubleJumpRight(r))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> Move.of(r, p)))
                .filter(this::isValid)
                .collect(toList());
        }
        
        private Optional<Position> doubleJumpUp(final Robot robot) {
            return doubleJump(robot, Position::up);
        }
        
        private Optional<Position> doubleJumpDown(final Robot robot) {
            return doubleJump(robot, Position::down);
        }
        
        private Optional<Position> doubleJumpLeft(final Robot robot) {
            return doubleJump(robot, Position::left);
        }
        
        private Optional<Position> doubleJumpRight(final Robot robot) {
            return doubleJump(robot, Position::right);
        }
        
        private Optional<Position> jumpUp(final Robot robot) {
            return jump(robot, Position::up);
        }
        
        private Optional<Position> jumpDown(final Robot robot) {
            return jump(robot, Position::down);
        }
        
        private Optional<Position> jumpLeft(final Robot robot) {
            return jump(robot, Position::left);
        }
        
        private Optional<Position> jumpRight(final Robot robot) {
            return jump(robot, Position::right);
        }

        private Optional<Position> jump(
                final Robot robot,
                final Function<Position, Position> move) {
            return Optional.of(robot.getPosition())
                    .map(move)
                    .filter(this::inBounds)
                    .filter(this::hasRobot)
                    .map(move)
                    .filter(this::inBounds)
                    .filter(this::isFree);
        }

        private Optional<Position> doubleJump(
                final Robot robot,
                final Function<Position, Position> move) {
            return Optional.of(robot.getPosition())
                    .map(move)
                    .filter(this::inBounds)
                    .filter(this::hasRobot)
                    .map(move)
                    .filter(this::inBounds)
                    .filter(this::hasRobot)
                    .map(move)
                    .filter(this::inBounds)
                    .filter(this::isFree);
        }
        
        private boolean inBounds(final Position position) {
            return 0 <= position.getRow() && position.getRow() < ROWS
                    && 0 <= position.getCol() && position.getCol() < COLS;
        }
        
        private boolean isValid(final Move move) {
            if (!inBounds(move.getTo())) {
                return false;
            }
            final Set<Position> adjacentSquares = this.adjacentSquares(move.getTo());
            if (move.getRobot().isBlue()) {
                return adjacentSquares.stream()
                        .noneMatch(p -> hasRedRobot(p) || hasBlueRobot(p));
            }
            if (move.getRobot().isRed()) {
                return adjacentSquares.stream()
                        .noneMatch(p -> hasBlueRobot(p));
            }
            return true;
        }
        
        private Set<Position> adjacentSquares(final Position position) {
            return Stream.of(
                    Position.of(position.getRow() - 1, position.getCol()),
                    Position.of(position.getRow() + 1, position.getCol()),
                    Position.of(position.getRow(), position.getCol() - 1),
                    Position.of(position.getRow(), position.getCol() + 1))
                .filter(this::inBounds)
                .collect(toSet());
        }
        
        private boolean hasRobot(final Position position) {
            return this.robots.stream()
                    .anyMatch(r -> r.getPosition().equals(position));
        }
        
        private boolean isFree(final Position position) {
            return !this.hasRobot(position);
        }
        
        private boolean hasRedRobot(final Position position) {
            return this.red.getPosition().equals(position);
        }
        
        private boolean hasBlueRobot(final Position position) {
            return this.blueRobots().stream()
                    .anyMatch(r -> r.getPosition().equals(position));
        }
        
        private Set<Robot> blueRobots() {
            return this.robots.stream().filter(Robot::isBlue).collect(toSet());
        }

        @Override
        public int hashCode() {
            return Objects.hash(robots);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Board other = (Board) obj;
            return Objects.equals(robots, other.robots);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Board [red=").append(red).append(", robots=").append(robots).append("]");
            return builder.toString();
        }

        public Robot getRed() {
            return this.red;
        }
    }
    
    private enum Color {
        RED, GREEN, BLUE;
    }

    private static final class Position {
        private final Integer row;
        private final Integer col;
        
        private Position(final Integer x, final Integer y) {
            this.row = x;
            this.col = y;
        }

        public static Position of(final Integer x, final Integer y) {
            return new Position(x, y);
        }
        
        public Position up() {
            return Position.of(this.row - 1, this.col);
        }

        public Position down() {
            return Position.of(this.row + 1, this.col);
        }
        
        public Position left() {
            return Position.of(this.row, this.col - 1);
        }
        
        public Position right() {
            return Position.of(this.row, this.col + 1);
        }
        
        public Integer getRow() {
            return row;
        }
        public Integer getCol() {
            return col;
        }
        
        public Integer manhattanDistance(final Position from) {
            return Math.abs(this.getRow() - from.getRow())
                    + Math.abs(this.getCol() - from.getCol());
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Position other = (Position) obj;
            return Objects.equals(row, other.row) && Objects.equals(col, other.col);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Position [row=").append(row).append(", col=").append(col).append("]");
            return builder.toString();
        }
        
    }
    
    private static final class Robot {
        private final Color color;
        private final Position position;

        private Robot(final Color color, final Position position) {
            this.color = color;
            this.position = position;
        }

        public static Robot red(final Integer row, final Integer col) {
            return new Robot(Color.RED, Position.of(row, col));
        }
        
        public static Robot green(final Integer row, final Integer col) {
            return new Robot(Color.GREEN, Position.of(row, col));
        }
        
        public static Robot blue(final Integer row, final Integer col) {
            return new Robot(Color.BLUE, Position.of(row, col));
        }
        
        public Robot withPosition(final Position position) {
            return new Robot(this.color, position);
        }
        
        public Integer distanceFromHome() {
            return this.position.manhattanDistance(Position.of(0, 0));
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(color, position);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Robot other = (Robot) obj;
            return color == other.color && Objects.equals(position, other.position);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Robot [color=").append(color).append(", position=").append(position).append("]");
            return builder.toString();
        }

        public Position getPosition() {
            return position;
        }
        
        
        public boolean isRed() {
            return this.color == Color.RED;
        }
        
        public boolean isBlue() {
            return this.color == Color.BLUE;
        }
    }
    
    private static final class Move {
        private final Robot robot;
        private final Position to;
        
        private Move(final Robot robot, final Position to) {
            this.robot = robot;
            this.to = to;
        }
        
        public static Move of(final Robot robot, final Position to) {
            return new Move(robot, to);
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Move [robot=").append(robot).append(", to=").append(to).append("]");
            return builder.toString();
        }

        public Position getTo() {
            return this.to;
        }

        public Robot getRobot() {
            return robot;
        }
    }
}
