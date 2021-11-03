
package benchmark;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.github.f4b6a3.ksuid.Ksuid;
import com.github.f4b6a3.ksuid.KsuidCreator;

@Fork(1)
@Threads(1)
@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {

	/* UUID */

	@Benchmark
	public UUID UUID_randomUUID() {
		return UUID.randomUUID();
	}

	@Benchmark
	public String UUID_randomUUID_toString() {
		return UUID.randomUUID().toString();
	}

//	@Benchmark
//	public UUID UUID_fromString() {
//		return UUID.fromString(uuidString);
//	}
//
//	@Benchmark
//	public String UUID_toString() {
//		return uuid.toString();
//	}
//
//	@Benchmark
//	public int UUID_compareTo() {
//		int c = 0;
//		for (int i = 0; i < UUID_SAMPLES.length; i++) {
//			c = UUID_SAMPLES[0].compareTo(UUID_SAMPLES[i]);
//		}
//		return c;
//	}

	/* KSUID */

	@Benchmark
	public Ksuid KsuidCreator_getKsuid() {
		return KsuidCreator.getKsuid();
	}

	@Benchmark
	public String KsuidCreator_getKsuid_toString() {
		return KsuidCreator.getKsuid().toString();
	}

//	@Benchmark
//	public Ksuid Ksuid_fromString() {
//		return Ksuid.from(ksuidString);
//	}
//
//	@Benchmark
//	public String Ksuid_toString() {
//		return ksuid.toString();
//	}
//
//	@Benchmark
//	public int Ksuid_compareTo() {
//		int c = 0;
//		for (int i = 0; i < KSUID_SAMPLES.length; i++) {
//			c = KSUID_SAMPLES[0].compareTo(KSUID_SAMPLES[i]);
//		}
//		return c;
//	}

	/* BENCHMARK SAMPLES */

	private static final UUID[] UUID_SAMPLES = new UUID[100];
	static {
		Random random = new Random();
		for (int i = 0; i < UUID_SAMPLES.length; i++) {
			UUID_SAMPLES[i] = new UUID(random.nextLong(), random.nextLong());
		}
	}

	private static final Ksuid[] KSUID_SAMPLES = new Ksuid[100];
	static {
		Random random = new Random();
		byte[] bytes = new byte[20];
		for (int i = 0; i < KSUID_SAMPLES.length; i++) {
			random.nextBytes(bytes);
			KSUID_SAMPLES[i] = Ksuid.from(bytes);
		}
	}

	private final UUID uuid = UUID_SAMPLES[0];
	private final String uuidString = UUID_SAMPLES[0].toString();

	private final Ksuid ksuid = KSUID_SAMPLES[0];
	private final String ksuidString = KSUID_SAMPLES[0].toString();
}
