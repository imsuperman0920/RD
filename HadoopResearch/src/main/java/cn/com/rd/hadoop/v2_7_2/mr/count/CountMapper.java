package cn.com.rd.hadoop.v2_7_2.mr.count;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * Mapper<Map输入key类型, Map输入value类型, Map输出key类型, Map输出value类型>
 * 输入key表示当前行的第一个byte是整个文件的第几个byte
 * @author i.m.superman
 */
public class CountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	/**
	 * 用于存储输出key
	 */
	private Text record = new Text();
	/**
	 * 用于累加结果值
	 */
	private static final IntWritable NUM_1 = new IntWritable(1);
	/** 
	   * Maps a single input key/value pair into an intermediate key/value pair.
	   * 
	   * <p>Output pairs need not be of the same types as input pairs.  A given 
	   * input pair may map to zero or many output pairs.  Output pairs are 
	   * collected with calls to 
	   * {@link OutputCollector#collect(Object,Object)}.</p>
	   *
	   * <p>Applications can use the {@link Reporter} provided to report progress 
	   * or just indicate that they are alive. In scenarios where the application 
	   * takes significant amount of time to process individual key/value
	   * pairs, this is crucial since the framework might assume that the task has 
	   * timed-out and kill that task. The other way of avoiding this is to set 
	   * <a href="{@docRoot}/../mapred-default.html#mapreduce.task.timeout">
	   * mapreduce.task.timeout</a> to a high-enough value (or even zero for no 
	   * time-outs).</p>
	   * 
	   * @param key the input key.
	   * @param value the input value.
	   * @param output collects mapped keys and values.
	   * @param reporter facility to report progress.
	   */
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		/*
		 *  没有配置 RecordReader，所以默认采用逐行读取
		 *  数据文件参见 440400.csv
		 *  key：行号
		 *  value：行信息
		 */
		String lineStr = value.toString();
		// 判空
		if(lineStr == null || "".equals(lineStr)) {
			return;
		}
		// 拆分行信息
		String[] infos = lineStr.split(",");
		// 城市代码
		String adcode = infos[1];
		// 道路等级
		String frc = infos[4];
		
		// 清空
		record.clear();
		// 设置输出key
		record.set("CityCode : " + adcode);
		// 标记输出key出现1次
		output.collect(record, NUM_1);
		
		// 清空
		record.clear();
		// 设置输出key
		record.set("FRC : " + frc);
		// 标记输出key出现1次
		output.collect(record, NUM_1);
	}
}
