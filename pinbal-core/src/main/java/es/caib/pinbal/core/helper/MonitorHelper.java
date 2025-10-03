package es.caib.pinbal.core.helper;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

/**
 * Monitor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@SuppressWarnings("restriction")
public class MonitorHelper {

	private static Boolean actiu = null;
	private static long prevUpTime;
	private static long	prevProcessCpuTime;
	private static RuntimeMXBean rmBean;
	private static com.sun.management.OperatingSystemMXBean sunOSMBean;
	private static final String NO_DISPONIBLE = "No disponible";

	private MonitorHelper() {
		throw new IllegalStateException("Utility class");
	}

	public static com.sun.management.OperatingSystemMXBean getSunOSMBean() {
		return sunOSMBean;
	}

	public static String getArch() {

		try {
			return sunOSMBean.getArch();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}
	
	public static String getName() {

		try {
			return sunOSMBean.getName();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}
	
	public static String getVersion() {

		try {
			return sunOSMBean.getVersion();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}

	private static Result result;
	public static Boolean getActiu() {
		return actiu;
	}

	private static class Result {
		long upTime = -1L;
		long processCpuTime = -1L;
		float cpuUsage = 0;
		int nCPUs;
	}

	static {
		try {
			rmBean = ManagementFactory.getRuntimeMXBean();
			// reperisco l'MBean relativo al sunOS
			sunOSMBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);

			result = new Result();
			result.nCPUs = sunOSMBean.getAvailableProcessors();
			result.upTime = rmBean.getUptime();
			result.processCpuTime = 0;
			if (sunOSMBean != null) {
				result.processCpuTime = sunOSMBean.getProcessCpuTime();
			}
		} catch (Exception e) {
			log.error(MonitorHelper.class.getSimpleName() + " exception ", e.getMessage());
		}
	}

	private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	public static long[] getThreadsIds() {
		return ManagementFactory.getThreadMXBean().getAllThreadIds();
	}

	public static String humanReadableByteCount(long bytes) {

		long unit = 1000;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		char pre = "kMGTPE".charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String getCPULoad() {

		try {
			result.upTime = rmBean.getUptime();
			result.processCpuTime = sunOSMBean.getProcessCpuTime();
			if (result.upTime > 0L && result.processCpuTime >= 0L) {
				updateCPUInfo();
			}
			return result.cpuUsage + "%";
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}

	public static void updateCPUInfo() {

		if (prevUpTime > 0L && result.upTime > prevUpTime) {
			long elapsedCpu = result.processCpuTime - prevProcessCpuTime;
			long elapsedTime = result.upTime - prevUpTime;
			result.cpuUsage = Math.round(Math.min(100F, elapsedCpu / (elapsedTime * 10000F * result.nCPUs)));
		}
		prevUpTime = result.upTime;
		prevProcessCpuTime = result.processCpuTime;
	}

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		long time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	public static long getCpuTimePercent() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		long time = 0L;
		long t;
		for (long i : getThreadsIds()) {
			t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get user time in nanoseconds. */
	public static long getUserTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
        long time = 0L;
		long t;
		for (long i : getThreadsIds()) {
			t = bean.getThreadUserTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
        long time = 0L;
		long tc;
		long tu;
		for (long i : getThreadsIds()) {
			tc = bean.getThreadCpuTime(i);
			tu = bean.getThreadUserTime(i);
			if (tc != -1 && tu != -1)
				time += (tc - tu);
		}
		return time;
	}
}
