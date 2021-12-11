
package benchmark;

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
@State(Scope.Benchmark)
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

	/* KSUID */

	@Benchmark
	public Ksuid KsuidCreator_getKsuid() {
		return KsuidCreator.getKsuid();
	}

	@Benchmark
	public String KsuidCreator_getKsuid_toString() {
		return KsuidCreator.getKsuid().toString();
	}

	@Benchmark
	public Ksuid KsuidCreator_getMonotonicKsuid() {
		return KsuidCreator.getMonotonicKsuid();
	}

	@Benchmark
	public String KsuidCreator_getMonotonicKsuid_toString() {
		return KsuidCreator.getMonotonicKsuid().toString();
	}
}
