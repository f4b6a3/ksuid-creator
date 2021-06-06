package com.github.f4b6a3.ksuid;

import java.util.HashSet;

import com.github.f4b6a3.ksuid.Ksuid;
import com.github.f4b6a3.ksuid.KsuidCreator;
import com.github.f4b6a3.ksuid.factory.KsuidFactory;

/**
 * 
 * This test starts many threads that keep requesting thousands of KSUIDs to a
 * single generator.
 * 
 * This is is not included in the {@link TestSuite} because it takes a long time
 * to finish.
 */
public class UniquenessTest {

	private int threadCount; // Number of threads to run
	private int requestCount; // Number of requests for thread

	// private long[][] cacheLong; // Store values generated per thread
	private HashSet<Ksuid> hashSet;

	private boolean verbose; // Show progress or not

	// KSUID factory
	private KsuidFactory factory;

	private long time = System.currentTimeMillis() / 1000; // fixed time in seconds

	/**
	 * Initialize the test.
	 * 
	 * @param threadCount
	 * @param requestCount
	 * @param factory
	 */
	public UniquenessTest(int threadCount, int requestCount, KsuidFactory factory, boolean progress) {
		this.threadCount = threadCount;
		this.requestCount = requestCount;
		this.factory = factory;
		this.verbose = progress;
		this.initCache();
	}

	private void initCache() {
		this.hashSet = new HashSet<>();
	}

	/**
	 * Initialize and start the threads.
	 */
	public void start() {

		Thread[] threads = new Thread[this.threadCount];

		// Instantiate and start many threads
		for (int i = 0; i < this.threadCount; i++) {
			threads[i] = new Thread(new UniquenessTestThread(i, verbose));
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public class UniquenessTestThread implements Runnable {

		private int id;
		private boolean verbose;

		public UniquenessTestThread(int id, boolean verbose) {
			this.id = id;
			this.verbose = verbose;
		}

		/**
		 * Run the test.
		 */
		@Override
		public void run() {

			int progress = 0;
			int max = requestCount;

			for (int i = 0; i < max; i++) {

				// Request a KSUID
				Ksuid ksuid = factory.create(time);

				if (verbose) {
					if (i % (max / 100) == 0) {
						// Calculate and show progress
						progress = (int) ((i * 1.0 / max) * 100);
						System.out.println(String.format("[Thread %06d] %s %s %s%%", id, ksuid, i, (int) progress));
					}
				}
				synchronized (hashSet) {
					// Insert the value in cache, if it does not exist in it.
					if (!hashSet.add(ksuid)) {
						System.err.println(
								String.format("[Thread %06d] %s %s %s%% [DUPLICATE]", id, ksuid, i, (int) progress));
					}
				}
			}

			if (verbose) {
				// Finished
				System.out.println(String.format("[Thread %06d] Done.", id));
			}
		}
	}

	public static void execute(boolean verbose, int threadCount, int requestCount) {
		KsuidFactory factory = KsuidCreator.getKsuidFactory();

		UniquenessTest test = new UniquenessTest(threadCount, requestCount, factory, verbose);
		test.start();
	}

	public static void main(String[] args) {
		boolean verbose = true;
		int threadCount = 16; // Number of threads to run
		int requestCount = 1_000_000; // Number of requests for thread
		execute(verbose, threadCount, requestCount);
	}
}
