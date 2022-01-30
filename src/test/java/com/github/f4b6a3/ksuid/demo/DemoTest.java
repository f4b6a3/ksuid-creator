package com.github.f4b6a3.ksuid.demo;

import com.github.f4b6a3.ksuid.KsuidCreator;

public class DemoTest {

	private static final String HORIZONTAL_LINE = "----------------------------------------";

	public static void printList() {
		int max = 100;

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### KSUID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(KsuidCreator.getKsuid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Subsecond KSUID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(KsuidCreator.getSubsecondKsuid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Monotonic KSUID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(KsuidCreator.getMonotonicKsuid());
		}
	}

	public static void main(String[] args) {
		printList();
	}
}
