package cn.com.rd.java.nio;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileReader {
	private final static String TESTINPUTFILE = "D:/mywork/data/apt.csv";

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();
		// Java8用流的方式读文件，更加高效
		List<String> resultList = Files.lines(Paths.get(TESTINPUTFILE), StandardCharsets.UTF_8).collect(Collectors.toList());
        System.out.println(resultList.size());
        System.out.println("take time : " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
