package cn.com.rd.hadoop.v2_7_2.mr.count;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 * Reducer<Map输出key类型, Map输出value类型, Reduce输出key类型, Reduce输出value类型>
 * @author i.m.superman
 */
public class CountReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
	/** 
	   * <i>Reduces</i> values for a given key.  
	   * 
	   * <p>The framework calls this method for each 
	   * <code>&lt;key, (list of values)&gt;</code> pair in the grouped inputs.
	   * Output values must be of the same type as input values.  Input keys must 
	   * not be altered. The framework will <b>reuse</b> the key and value objects
	   * that are passed into the reduce, therefore the application should clone
	   * the objects they want to keep a copy of. In many cases, all values are 
	   * combined into zero or one value.
	   * </p>
	   *   
	   * <p>Output pairs are collected with calls to  
	   * {@link OutputCollector#collect(Object,Object)}.</p>
	   *
	   * <p>Applications can use the {@link Reporter} provided to report progress 
	   * or just indicate that they are alive. In scenarios where the application 
	   * takes a significant amount of time to process individual key/value 
	   * pairs, this is crucial since the framework might assume that the task has 
	   * timed-out and kill that task. The other way of avoiding this is to set 
	   * <a href="{@docRoot}/../mapred-default.html#mapreduce.task.timeout">
	   * mapreduce.task.timeout</a> to a high-enough value (or even zero for no 
	   * time-outs).</p>
	   * 
	   * @param key the key.
	   * @param values the list of values to reduce.
	   * @param output to collect keys and combined values.
	   * @param reporter facility to report progress.
	   */
	public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		// 用于记录累加的结果值
		int sumResult = 0;
		// 遍历 Map 结果集
		while(values.hasNext()) {
			// 累加次数值
			sumResult += values.next().get();
		}
		// 将 key 和累加后的次数和加入输出对象中
		output.collect(key, new IntWritable(sumResult));
	}
}
