package cn.com.rd.hadoop.v2_7_2.mr.count;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Partitioner;

/**
 * Partitioner<Map输出key类型, Map输出value类型>
 * @author i.m.superman
 */
public class CountPartitioner extends MapReduceBase implements Partitioner<Text, IntWritable> {
	/** 
	   * Get the paritition number for a given key (hence record) given the total 
	   * number of partitions i.e. number of reduce-tasks for the job.
	   *   
	   * <p>Typically a hash function on a all or a subset of the key.</p>
	   *
	   * @param key the key to be paritioned.
	   * @param value the entry value.
	   * @param numPartitions the total number of partitions.
	   * @return the partition number for the <code>key</code>.
	   */
	public int getPartition(Text key, IntWritable value, int numPartitions) {
		/*
		 * 因为需要按 CityCode 和 FRC 两个维度统计，所以按分区号安排统计项
		 * 判断分区数
		 */
		if(numPartitions >= 2) {
			// 判断输出key
			if(key.toString().startsWith("CityCode")) {
				// 按 CityCode 的统计分配到 0 分区
				return 0;
			} else {
				// 其他（按 FRC）的统计分配到 1 分区
				return 1;
			}
		} else {
			// 如果只有1个分区，就不需要按分区分配了 
			return 0;
		}
	}
}
