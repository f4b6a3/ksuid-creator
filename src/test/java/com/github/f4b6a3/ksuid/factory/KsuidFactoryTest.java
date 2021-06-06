package com.github.f4b6a3.ksuid.factory;

import org.junit.Test;

import com.github.f4b6a3.ksuid.Ksuid;
import com.github.f4b6a3.ksuid.KsuidCreator;
import com.github.f4b6a3.ksuid.factory.KsuidFactory;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	@Test
	public void testGetKsuidMs() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getKsuidMs();
		}

		long endTime = System.currentTimeMillis() / 1000;

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	@Test
	public void testGetKsuidUs() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getKsuidUs();
		}

		long endTime = System.currentTimeMillis() / 1000;

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	@Test
	public void testGetKsuidNs() {
		Ksuid[] list = new Ksuid[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis() / 1000;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = KsuidCreator.getKsuidNs();
		}

		long endTime = System.currentTimeMillis() / 1000;

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	private void checkNullOrInvalid(Ksuid[] list) {
		for (Ksuid ksuid : list) {
			assertNotNull("KSUID is null", ksuid);
		}
	}

	private void checkUniqueness(Ksuid[] list) {

		HashSet<Ksuid> set = new HashSet<>();

		for (Ksuid ksuid : list) {
			assertTrue(String.format("KSUID is duplicated %s", ksuid), set.add(ksuid));
		}

		assertEquals("There are duplicated KSUIDs", set.size(), list.length);
	}

	private void checkCreationTime(Ksuid[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (Ksuid ksuid : list) {
			long creationTime = ksuid.getTime();
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}
	}

	@Test
	public void testGetKsuidInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			Random random = new Random();
			KsuidFactory factory = KsuidCreator.getKsuidFactory().withRandomGenerator(random::nextBytes);
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
			long time = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getKsuid(time);
			assertEquals(Instant.ofEpochSecond(time), ksuid.getInstant());
		}
	}

	@Test
	public void testGetKsuidTime() {
		for (int i = 0; i < 100; i++) {
			long time = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			Ksuid ksuid = KsuidCreator.getKsuid(time);
			assertEquals(time, ksuid.getTime());
		}
	}

	@Test
	public void testGetKsuidTimeMs() {
		for (int i = 0; i < 100; i++) {
			long time = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int ms = (RANDOM.nextInt() & 0x7fffffff) % 1000;

			Ksuid ksuid = KsuidCreator.getKsuidMs(time, ms);
			assertEquals(time, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadMs = (((payload[0] & 0xff) << 8) | (payload[1] & 0xff)) >>> 6;
			assertEquals(ms, payloadMs);
		}
	}

	@Test
	public void testGetKsuidTimeUs() {
		for (int i = 0; i < 100; i++) {
			long time = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int us = (RANDOM.nextInt() & 0x7fffffff) % 1_000_000;

			Ksuid ksuid = KsuidCreator.getKsuidUs(time, us);
			assertEquals(time, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadUs = (((payload[0] & 0xff) << 16) | ((payload[1] & 0xff) << 8) | (payload[2] & 0xff)) >>> 4;
			assertEquals(us, payloadUs);
		}
	}

	@Test
	public void testGetKsuidTimeNs() {
		for (int i = 0; i < 100; i++) {
			long time = (RANDOM.nextLong() & 0x00000000ffffffffL) + Ksuid.EPOCH_OFFSET;
			int ns = (RANDOM.nextInt() & 0x7fffffff) % 1_000_000_000;

			Ksuid ksuid = KsuidCreator.getKsuidNs(time, ns);
			assertEquals(time, ksuid.getTime());

			byte[] payload = ksuid.getPayload();
			int payloadNs = (((payload[0] & 0xff) << 24) | ((payload[1] & 0xff) << 16) | ((payload[2] & 0xff) << 8)
					| (payload[3] & 0xff)) >>> 2;
			assertEquals(ns, payloadNs);
		}
	}

	protected static class TestThread extends Thread {

		public static Set<Ksuid> hashSet = new HashSet<>();
		private KsuidFactory creator;
		private int loopLimit;

		public TestThread(KsuidFactory creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			long time = System.currentTimeMillis() / 1000;
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create(time));
				}
			}
		}
	}
}
