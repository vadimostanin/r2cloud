package ru.r2cloud.tle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ru.r2cloud.TestConfiguration;
import ru.r2cloud.util.Clock;
import ru.r2cloud.util.ThreadPoolFactory;

public class TLEReloaderTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private TLEDao tleDao;
	private Clock clock;
	private ThreadPoolFactory threadPool;
	private ScheduledExecutorService executor;
	private long current;
	private TestConfiguration config;

	@Test
	public void testSuccess() throws Exception {
		TLEReloader reloader = new TLEReloader(config, tleDao, threadPool, clock);
		reloader.start();

		verify(clock).millis();
		verify(executor).scheduleAtFixedRate(any(), anyLong(), anyLong(), any());
	}

	@Test
	public void testLifecycle() {
		TLEReloader reloader = new TLEReloader(config, tleDao, threadPool, clock);
		reloader.start();
		reloader.start();
		verify(executor, times(1)).scheduleAtFixedRate(any(), anyLong(), anyLong(), any());
	}

	@Before
	public void start() throws Exception {
		config = new TestConfiguration(tempFolder);
		tleDao = mock(TLEDao.class);
		clock = mock(Clock.class);
		threadPool = mock(ThreadPoolFactory.class);
		executor = mock(ScheduledExecutorService.class);
		when(threadPool.newScheduledThreadPool(anyInt(), any())).thenReturn(executor);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, SSS");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		current = sdf.parse("2017-10-23 00:00:00, 000").getTime();

		when(clock.millis()).thenReturn(current);

	}
}
