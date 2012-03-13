import junit.framework.Assert;

public class PRGCracker {

    private static final long P = 295075153L;

    private static final long LIMIT = 1L << 32;

    long solution = 231886864;

    public static void main(String[] args) {
        new PRGCracker().start();
    }

    long[] sequence = new long[] { 210205973L, 22795300L, 58776750L, 121262470L, 264731963L, 140842553L, 242590528L,
            195244728L, 86752752L };

    private long xGuess, nextx = 0, nexty = 0;

    private void start() {
        boolean success = false;
        xGuess = -1;
        long lasttime = System.currentTimeMillis();
        Generator generator = new Generator();
        do {
            xGuess++;
            if (xGuess % 1000 == 0) {
                long now = System.currentTimeMillis();
                if ((now - lasttime) / 1000 > 10) {
                    System.out.println("Processing x= " + xGuess + " (" + (xGuess * 100f / LIMIT) + "%)");
                    lasttime = now;
                }
            }
            calcNextY();
            success = true;
            generator.seed(nextx, nexty);
            int i = 1;
            for (; success && i < sequence.length; i++) {
                if (sequence[i] != generator.next()) {
                    success = false;
                    i--;
                }
            }
            if (success) {
                long y = guessInitialY();
                if (y > 0) {
                    System.out.println("WOW, found x, y: " + xGuess + ", " + y);
                    generator.seed(xGuess, y);
                    for (i = 0; i < sequence.length; i++) {
                        long next = generator.next();
                        Assert.assertEquals(sequence[i], next);
                        System.out.println(next);
                    }
                    System.out.println(generator.next() + " <<");
                }
            }
        } while (xGuess <= LIMIT);
    }

    private long guessInitialY() {
        Generator generator = new Generator();
        long y = 0;
        boolean success;
        do {
            long y1 = (3 * y + 7) % P;
            success = y1 == nexty;
            if (success) {
                generator.seed(xGuess, y);
                success = generator.next() == sequence[0];
            } else {
                y++;
            }
        } while (!success && y < LIMIT);
        if (success) {
            return y;
        } else {
            return 0;
        }
    }

    private void calcNextY() {
        nextx = (2 * xGuess + 5) % P;
        nexty = nextx ^ sequence[0];
        if (((nexty - 7) % 3) != 0) {
            //
        }
    }

    private static class Generator {

        private long x;

        private long y;

        public void seed(long x, long y) {
            this.x = x;
            this.y = y;
        }

        public long next() {
            x = (2 * x + 5) % P;
            y = (3 * y + 7) % P;
            long z = x ^ y;
            return z;
        }
    }
}
