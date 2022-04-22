package com.github.f4b6a3.ksuid;

import org.junit.Test;

import com.github.f4b6a3.ksuid.Ksuid;
import com.github.f4b6a3.ksuid.KsuidCreator;
import com.github.f4b6a3.ksuid.KsuidFactory;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class KsuidFactoryTest {

	protected static final int DEFAULT_LOOP_MAX = 10_000;

	protected static final String DUPLICATE_UUID_MSG = "A duplicate KSUID was created.";

	protected static final int THREAD_TOTAL = availableProcessors();

	protected static final Random RANDOM = new Random();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	@Test
	public void testGetKsuid() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getKsuid();
		}

		long endTime = System.currentTimeMillis() / 1000;

		assertTrue(checkNullOrInvalid(list));
		assertTrue(checkUniqueness(list));
		assertTrue(checkCreationTime(list, startTime, endTime));
	}

	@Test
	public void testGetSubsecondKsuid() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getSubsecondKsuid();
		}

		long endTime = System.currentTimeMillis() / 1000;

		assertTrue(checkNullOrInvalid(list));
		assertTrue(checkUniqueness(list));
		assertTrue(checkCreationTime(list, startTime, endTime));
	}

	@Test
	public void testGetMonotonicKsuid() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getMonotonicKsuid();
		}

		long endTime = System.currentTimeMillis() / 1000;

		assertTrue(checkNullOrInvalid(list));
		assertTrue(checkUniqueness(list));
		assertTrue(checkCreationTime(list, startTime, endTime));
	}

	private boolean checkNullOrInvalid(Ksuid[] list) {
		for (Ksuid ksuid : list) {
			assertNotNull("KSUID is null", ksuid);
		}
		return true; // success
	}

	private boolean checkUniqueness(Ksuid[] list) {

		HashSet<Ksuid> set = new HashSet<>();

		for (Ksuid ksuid : list) {
			assertTrue(String.format("KSUID is duplicated %s", ksuid), set.add(ksuid));
		}

		assertEquals("There are duplicated KSUIDs", set.size(), list.length);

		return true; // success
	}

	private boolean checkCreationTime(Ksuid[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (Ksuid ksuid : list) {
			long creationTime = ksuid.getTime();
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

		return true; // success
	}

	@Test
	public void testGetKsuidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			KsuidFactory factory = KsuidFactory.newInstance(new Random());
			threads[i] = new TestThread(factory, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique KSUID is correct
		assertEquals(DUPLICATE_UUID_MSG + " " + TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL),
				TestThread.hashSet.size());
	}

	@Test
	public void testGetMonotonicKsuidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			KsuidFactory factory = KsuidFactory.newMonotonicInstance(new Random());
			threads[i] = new TestThread(factory, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique KSUID is correct
		assertEquals(DUPLICATE_UUID_MSG + " " + TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL),
				TestThread.hashSet.size());
	}

	@Test
	public void testGetSubsecondKsuidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			KsuidFactory factory = KsuidFactory.newSubsecondInstance(new Random());
			threads[i] = new TestThread(factory, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique KSUID is correct
		assertEquals(DUPLICATE_UUID_MSG + " " + TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL),
				TestThread.hashSet.size());
	}

	@Test
	public void testGetKsuidInstant() {
		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getKsuid(Instant.ofEpochSecond(seconds));
			assertEquals(Instant.ofEpochSecond(seconds), ksuid.getInstant());
		}
	}

	@Test
	public void testGetKsuidTime() {
		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getKsuid(Instant.ofEpochSecond(seconds));
			assertEquals(seconds, ksuid.getTime());
		}
	}

	@Test
	public void testGetSubsecondKsuidTime() {
		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getSubsecondKsuid(Instant.ofEpochSecond(seconds));
			assertEquals(seconds, ksuid.getTime());
		}
	}

	@Test
	public void testGetSubsecondKsuidMillisecond() {

		Supplier<byte[]> supplier = KsuidFactory.getRandomSupplier(new Random());
		Function<Instant, Ksuid> function = new KsuidFactory.MillisecondFunction(supplier);
		KsuidFactory factory = new KsuidFactory(function);

		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int ms = (RANDOM.nextInt() & 0x7fffffff) % 1000;

			Ksuid ksuid = factory.create(Instant.ofEpochSecond(seconds).plusNanos(ms * 1000000));
			assertEquals(seconds, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadMs = (((payload[0] & 0xff) << 8) | (payload[1] & 0xff)) >>> 6;
			assertEquals(ms, payloadMs);
		}
	}

	@Test
	public void testGetSubsecondKsuidMicrosecond() {

		Supplier<byte[]> supplier = KsuidFactory.getRandomSupplier(new Random());
		Function<Instant, Ksuid> function = new KsuidFactory.MicrosecondFunction(supplier);
		KsuidFactory factory = new KsuidFactory(function);

		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int us = (RANDOM.nextInt() & 0x7fffffff) % 1_000_000;

			Ksuid ksuid = factory.create(Instant.ofEpochSecond(seconds).plusNanos(us * 1000));
			assertEquals(seconds, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadUs = (((payload[0] & 0xff) << 16) | ((payload[1] & 0xff) << 8) | (payload[2] & 0xff)) >>> 4;
			assertEquals(us, payloadUs);
		}
	}

	@Test
	public void testGetSubsecondKsuidNanosecond() {

		Supplier<byte[]> supplier = KsuidFactory.getRandomSupplier(new Random());
		Function<Instant, Ksuid> function = new KsuidFactory.NanosecondFunction(supplier);
		KsuidFactory factory = new KsuidFactory(function);

		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int ns = (RANDOM.nextInt() & 0x7fffffff) % 1_000_000_000;

			Ksuid ksuid = factory.create(Instant.ofEpochSecond(seconds).plusNanos(ns));
			assertEquals(seconds, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadNs = (((payload[0] & 0xff) << 24) | ((payload[1] & 0xff) << 16) | ((payload[2] & 0xff) << 8)
					| (payload[3] & 0xff)) >>> 2;
			assertEquals(ns, payloadNs);
		}
	}

	@Test
	public void testGetMonotonicKsuidTime() {
		for (int i = 0; i < 100; i++) {
			long seconds = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getMonotonicKsuid(Instant.ofEpochSecond(seconds));
			assertEquals(seconds, ksuid.getTime());
		}
	}

	@Test
	public void testGetSubsecondPrecision() {

		int loop = 10;
		int precision;
		Clock clock;

		for (int i = 0; i < loop; i++) {
			clock = getClock(KsuidFactory.PRECISION_MILLISECOND);
			precision = KsuidFactory.getSubsecondPrecision(clock);
			assertEquals(KsuidFactory.PRECISION_MILLISECOND, precision);
		}

		for (int i = 0; i < loop; i++) {
			clock = getClock(KsuidFactory.PRECISION_MICROSECOND);
			precision = KsuidFactory.getSubsecondPrecision(clock);
			assertEquals(KsuidFactory.PRECISION_MICROSECOND, precision);
		}

		for (int i = 0; i < loop; i++) {
			clock = getClock(KsuidFactory.PRECISION_NANOSECOND);
			precision = KsuidFactory.getSubsecondPrecision(clock);
			assertEquals(KsuidFactory.PRECISION_NANOSECOND, precision);
		}
	}

	@Test
	public void testGetMonotonicKsuidAfterClockDrift() {

		long diff = KsuidFactory.MonotonicFunction.CLOCK_DRIFT_TOLERANCE;
		long time = Instant.parse("2021-12-31T23:59:59.000Z").getEpochSecond();
		long times[] = { time, time + 0, time + 1, time + 2, time + 3 - diff, time + 4 - diff, time + 5 };

		Clock clock = new Clock() {
			private int i;

			@Override
			public Instant instant() {
				return Instant.ofEpochSecond(times[i++ % times.length]);
			}

			@Override
			public ZoneId getZone() {
				return null;
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}
		};

		Supplier<byte[]> randomSupplier = KsuidFactory.getRandomSupplier(new Random());
		KsuidFactory factory = KsuidFactory.newMonotonicInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // time
		long ms2 = factory.create().getTime(); // time + 0
		long ms3 = factory.create().getTime(); // time + 1
		long ms4 = factory.create().getTime(); // time + 2
		long ms5 = factory.create().getTime(); // time + 3 - 10000 (CLOCK DRIFT)
		long ms6 = factory.create().getTime(); // time + 4 - 10000 (CLOCK DRIFT)
		long ms7 = factory.create().getTime(); // time + 5
		assertEquals(ms1 + 0, ms2); // clock repeats.
		assertEquals(ms1 + 1, ms3); // clock advanced.
		assertEquals(ms1 + 2, ms4); // clock advanced.
		assertEquals(ms1 + 2, ms5); // CLOCK DRIFT! DON'T MOVE BACKWARDS!
		assertEquals(ms1 + 2, ms6); // CLOCK DRIFT! DON'T MOVE BACKWARDS!
		assertEquals(ms1 + 5, ms7); // clock advanced.
	}

	@Test
	public void testGetMonotonicKsuidAfterLeapSecond() {

		long second = Instant.parse("2021-12-31T23:59:59.000Z").getEpochSecond();
		long leapSecond = second - 1; // simulate a leap second
		long times[] = { second, leapSecond };

		Clock clock = new Clock() {
			private int i;

			@Override
			public Instant instant() {
				return Instant.ofEpochSecond(times[i++ % times.length]);
			}

			@Override
			public ZoneId getZone() {
				return null;
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}
		};

		Supplier<byte[]> randomSupplier = KsuidFactory.getRandomSupplier(new Random());
		KsuidFactory factory = KsuidFactory.newMonotonicInstance(randomSupplier, clock);

		long ms1 = factory.create().getTime(); // second
		long ms2 = factory.create().getTime(); // leap second

		assertEquals(ms1, ms2); // LEAP SECOND! DON'T MOVE BACKWARDS!
	}

	private Clock getClock(int precision) {

		final int divisor;

		switch (precision) {
		case KsuidFactory.PRECISION_MILLISECOND:
			divisor = 1_000_000;
			break;
		case KsuidFactory.PRECISION_MICROSECOND:
			divisor = 1_000;
			break;
		case KsuidFactory.PRECISION_NANOSECOND:
			divisor = 1;
			break;
		default:
			divisor = 1;
			break;
		}

		return new Clock() {

			@Override
			public Instant instant() {
				long second = System.currentTimeMillis() / 1000;
				long random = RANDOM.nextInt(1_000_000_000);
				long adjust = random - (random % divisor);

				// return an instant with a custom precision
				return Instant.ofEpochSecond(second, adjust);
			}

			@Override
			public Clock withZone(ZoneId zone) {
				return null;
			}

			@Override
			public ZoneId getZone() {
				return null;
			}
		};

	}

	protected static class TestThread extends Thread {

		public static Set<Ksuid> hashSet = new HashSet<>();
		private KsuidFactory factory;
		private int loopLimit;

		public TestThread(KsuidFactory factory, int loopLimit) {
			this.factory = factory;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			Instant time = Instant.now();
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(factory.create(time));
				}
			}
		}
	}
}
