package cn.com.qr.java.fj;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class TestFJ extends RecursiveTask<Long> {
	private static final long serialVersionUID = 1L;
	/**
	 * 起始数
	 */
	private long startNum = 0;
	/**
	 * 终止数
	 */
	private long endNum = 0;
	/**
	 * 分割阀值
	 */
	private long threshold = 0;
	
	/**
	 * 带参构造器
	 * @param startNum  起始数
	 * @param endNum   终止数
	 * @param threshold  分割阀值
	 */
	public TestFJ(long startNum, long endNum, long threshold) {
		this.startNum = startNum;
		this.endNum = endNum;
		this.threshold = threshold;
	}

	@Override
	protected Long compute() {
		// 最终结果值
		long sum = 0;
		// 根据分割阀值判断是否直接计算
		boolean canCompute = (endNum - startNum) < threshold;
		
		if(canCompute) {
			/* 直接计算则进行累加 */
			for(long i = startNum;i <= endNum;i ++) {
				sum += i;
			}
		} else {
			/* 分割成两个 Task */
			long middle = (startNum + endNum) / 2;
			// 创建子任务对象
			TestFJ subTask1 = new TestFJ(startNum, middle, threshold);
			TestFJ subTask2 = new TestFJ(middle + 1, endNum, threshold);
			// 执行子任务
			invokeAll(subTask1, subTask2);
//			subTask1.fork();
//			subTask2.fork();
			// 累加子任务结果
			sum = subTask1.join() + subTask2.join();
		}
		// 返回最终结果值
		return sum;
	}
	
	
	/**
	 * 测试主方法
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		/*
		 *  创建工作线程池
		 *  可以使用带参数的构造器来指定工作线程数
		 *  默认值为 CPU 核心数（Runtime.getRuntime().availableProcessors()）
		 */
		ForkJoinPool fjPool = new ForkJoinPool();
		
		long startTime = System.currentTimeMillis();
		
		TestFJ testFJ = new TestFJ(1, 10000000, 6000000);
		Future<Long> result = fjPool.submit(testFJ);
		
		System.out.println("FJ result [" + result.get() +  "]" + "\t\tTime [" + (System.currentTimeMillis() - startTime) + "ms]");
		
		
		startTime = System.currentTimeMillis();
		long resultNum = 0;
		for(int i = 1;i <= 10000000;i ++) {
			resultNum += i;
		}
		System.out.println("LOOP result [" + resultNum +  "]" + "\t\tTime [" + (System.currentTimeMillis() - startTime) + "ms]");
	}
}
