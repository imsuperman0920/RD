package cn.com.rd.java.fj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class FJ_CountLinkLength extends RecursiveTask<Map<Integer, Integer>> {
	private static final long serialVersionUID = 1L;
	
	private static File OPLRMT = new File("D:/mywork/data/openlr/link_name_16Q1_108city.csv");
//	private static File OPLRMT = new File("D:/mywork/data/openlr/test1.csv");
	
	/**
	 * 起始数
	 */
	private int startNum = 0;
	/**
	 * 终止数
	 */
	private int endNum = 0;
	/**
	 * 分割阀值
	 */
	private int threshold = 0;
	/**
	 * 数据信息列表
	 */
	private List<String> infoList = new ArrayList<String>();
	
	/**
	 * 带参构造器
	 * @param startNum  起始数
	 * @param endNum   终止数
	 * @param threshold  分割阀值
	 */
	public FJ_CountLinkLength(int startNum, int endNum, int threshold, List<String> infoList) {
		this.startNum = startNum;
		this.endNum = endNum;
		this.threshold = threshold;
		this.infoList = infoList;
	}
	
	@Override
	protected Map<Integer, Integer> compute() {
		// 最终结果对应表
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		// 根据分割阀值判断是否直接计算
		boolean canCompute = (endNum - startNum) < threshold;
		
		if(canCompute) {
			/* 直接进行长度统计 */
			for(int i = startNum;i <= endNum;i ++) {
				/*
				 * 0   routeid
				 * 1   adcode
				 * 2   TMC范围内（0否，1是）
				 * 3   短小link标记（0否，1是）
				 * 4   frc
				 * 5   kind
				 * 6   linkid1;length1|linkid2;length2|...|
				 * 7   头接续 routeid
				 * 8   尾接续 routeid
				 * 9   头是否接续TMC
				 * 10 尾是否接续TMC
				 */
				String[] infos = infoList.get(i).split(",");
				// 只统计 TMC 范围外或范围内的匝道
				if(!("0".equals(infos[2]) || (isUnlimited(infos[5])))) {
					continue;
				}
				
				// 城市代码
				int adcode = Integer.parseInt(infos[1]);
				// 城市公里数
				int cityLen = countKM(infos[6]);
				if(resultMap.get(adcode) != null) {
					cityLen += resultMap.get(adcode);
				}
				resultMap.put(adcode, cityLen);
			}
		} else {
			/* 分割成两个 Task */
			int middle = (startNum + endNum) / 2;
			// 创建子任务对象
			FJ_CountLinkLength subTask1 = new FJ_CountLinkLength(startNum, middle, threshold, infoList);
			FJ_CountLinkLength subTask2 = new FJ_CountLinkLength(middle + 1, endNum, threshold, infoList);
			// 执行子任务
			invokeAll(subTask1, subTask2);
			// 累加子任务结果
			Map<Integer, Integer> subMap1 = subTask1.join();
			Map<Integer, Integer> subMap2 = subTask2.join();
			resultMap.putAll(subMap1);
			for(int adcode : subMap2.keySet()) {
				if(resultMap.get(adcode) != null) {
					resultMap.put(adcode, resultMap.get(adcode) + subMap2.get(adcode));
				} else {
					resultMap.put(adcode, subMap2.get(adcode));
				}
			}
		}
		
		// 返回最终结果对应表
		return resultMap;
	}
	
	/**
	 * 判断是否是可以取消过滤的类型
	 * @param oplrMT
	 * @return
	 */
	private boolean isUnlimited(String kindStr) {
//		if("".equals(kindStr)) return true;
		
		boolean unlimitedTypes = false;
		// 道路类别
        String[] routeKinds = kindStr.split("\\|");
        // 遍历道路类别
        for(String routeKind : routeKinds) {
        	// 道路类别（XXYY）XX道路等级；YY道路类型
        	String kind = routeKind.toLowerCase().substring(2);
        	// 判断道路类别是否是无限制类型
        	if("05".equals(kind) || "03".equals(kind) || "0b".equals(kind)) {
        		// 跳出循环，更改标识
        		unlimitedTypes = true;
        		break;
        	}
		}
        return unlimitedTypes;
	}
	
	private int countKM(String linkAndLenStr) {
		int result = 0;
		String[] linkAndLen = linkAndLenStr.split("\\|");
		for(String str : linkAndLen) {
			String[] strArray = str.split(";");
			if(strArray.length == 2) {
				int len = Integer.parseInt(strArray[1]);
				result += len;
			}
		}
		return result / 1000;
	}
	
	
	/**
	 * 测试主方法
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		List<String> cityMTList = makeCityMTList();
		long startTime = System.currentTimeMillis();
		/*
		 *  创建工作线程池
		 *  可以使用带参数的构造器来指定工作线程数
		 *  默认值为 CPU 核心数（Runtime.getRuntime().availableProcessors()）
		 */
		ForkJoinPool fjPool = new ForkJoinPool();
		
		FJ_CountLinkLength testFJ = new FJ_CountLinkLength(0, cityMTList.size() - 1, 50, cityMTList);
		Future<Map<Integer, Integer>> result = fjPool.submit(testFJ);
		
		System.out.println("FJ result [" + result.get().toString() +  "]" + "\t\tTime [" + (System.currentTimeMillis() - startTime) + "ms]");
	}
	
	private static List<String> makeCityMTList() throws Exception {
		List<String> list = new ArrayList<String>();
		InputStream is = new FileInputStream(OPLRMT);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
		isr.close();
		is.close();
		return list;
	}
}
