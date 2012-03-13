import java.util.ArrayList;
import java.util.List;

public class PRGCracker {

	private static final long P = 295075153L;

	private static final long LIMIT = 1L << 32;

	public static void main(String[] args) {
		new PRGCracker().start();
	}

	long[] sequence = new long[] { 210205973L, 22795300L, 58776750L, 121262470L,
			264731963L, 140842553L, 242590528L, 195244728L, 86752752L };

	private void start() {
		boolean success = false;
		long x = -1, y = 0;
		long lasttime = System.currentTimeMillis();
		Generator generator = new Generator();
		do {
			x++;
			if (x % 1000 == 0) {
				long now = System.currentTimeMillis();
				if ((now - lasttime) / 1000 > 10) {
					System.out.println("Processing x= " + x + " ("
							+ (x * 100f / LIMIT) + "%)");
					lasttime = now;
				}
			}
			long[] ys = guessY(x);
			for (int yloop = 0; !success && yloop < ys.length; yloop++) {
				y = ys[yloop];
				success = true;
				generator.seed(x, y);
				int i = 0;
				for (; success && i < sequence.length; i++) {
					if (sequence[i] != generator.next()) {
						success = false;
						i--;
					}
				}
				if (i > 1 && !success) {
					System.out.println("WOW, found a pair that survived " + i
							+ " rounds! x = " + x);
				}

			}

		} while (!success && x <= LIMIT);
		if (success) {
			System.out.println("Found x, y: " + x + ", " + y);
			System.out.println("Next number is " + generator.next());
		} else {
			System.out.println("What a pity, nothing found... :-(");
		}
	}


	private long[] guessY(long x) {
		long nextx = (2 * x + 5) % P;
		long nexty = nextx ^ sequence[0];
		if (((nexty - 7) % 3) != 0) {
			return new long[0];
		}
		List<Long> result = new ArrayList<Long>();
		long y = ((nexty - 7) / 3) % P;
		while (y < LIMIT) {
			result.add(y);
			y += P;
		}
		long[] resultAsArray = new long[result.size()];
		int i = 0;
		for (Long long1 : result) {
			resultAsArray[i++] = long1;
		}
		return resultAsArray;
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
