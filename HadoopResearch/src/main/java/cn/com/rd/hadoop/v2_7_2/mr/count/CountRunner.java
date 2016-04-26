package cn.com.rd.hadoop.v2_7_2.mr.count;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 执行类
 * 使用 Hadoop 提供的工具包，方便启动和配置
 * @author i.m.superman
 */
public class CountRunner extends Configured implements Tool {
	/**
	   * Execute the command with the given arguments.
	   * 
	   * @param args command specific arguments.
	   * @return exit code.
	   * @throws Exception
	   */
	public int run(String[] args) throws Exception {
		// 传入参数校验
		if(args == null || args.length < 2) {
			return 1;
		}
		// job 属性信息设置
		JobConf jobConf = new JobConf(getConf(), CountRunner.class);
		// 设置 job 名称
		jobConf.setJobName("CountRunner");
		// 设置 Mapper 类
		jobConf.setMapperClass(CountMapper.class);
		// 设置 Partitioner 类
		jobConf.setPartitionerClass(CountPartitioner.class);
		// 设置 Reducer 类
		jobConf.setReducerClass(CountReducer.class);
		// 强制设定两个分区，分别处理两个维度（CityCode 和 FRC）的统计
		jobConf.setNumReduceTasks(2);
		/*
		 *  设置输入路径
		 *  该路径为 HDFS 路径
		 */
		FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
		/*
		 *  设置输出路径
		 *  该路径为 HDFS 路径
		 */
		FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));
		
		// 执行 job
		JobClient.runJob(jobConf);
		// 返回退出状态值
		return 0;
	}
	/**
	 * 入口方法
	 * @param args 外部传入的参数数组   {"输入文件路径", "输出路径"}
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*
		 *  调用工具方法启动 MR，返回退出状态码
		 *  退出的状态码为 0，表示正常退出；非 0，为异常退出
		 */
		int exitCode = ToolRunner.run(new Configuration(), new CountRunner(), args);
		System.exit(exitCode);
	}
}
