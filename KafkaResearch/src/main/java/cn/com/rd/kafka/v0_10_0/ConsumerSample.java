package cn.com.rd.kafka.v0_10_0;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerSample {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
	    // 连接服务器地址
		props.put("bootstrap.servers", "rdmax:9092");
	    props.put("group.id", "testGroup1");
	    props.put("enable.auto.commit", "false");
//	    props.put("auto.commit.interval.ms", 1000);
	    props.put("session.timeout.ms", 30000);
//	    props.put("request.timeout.ms", 60000);
//	    props.put("exclude.internal.topics", "false");
	    props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
	    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
	    KafkaConsumer<Integer, byte[]> consumer = new KafkaConsumer<>(props);
	    consumer.subscribe(Arrays.asList("RP10000012F901", "RP10000012F902"));
	    while (true) {
	        ConsumerRecords<Integer, byte[]> records = consumer.poll(0);
	        for (ConsumerRecord<Integer, byte[]> record : records) {
	            System.out.printf("timestamp = %d, partition = %d, offset = %d, key = %s, value = %s", record.timestamp(), record.partition(), record.offset(), record.key(), record.value());
	            System.out.println();
	        }
	        // 异步提交
//	        consumer.commitAsync(new OffsetCommitCallback() {
//										        	public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
//										        		if (exception != null) {
//										        			System.out.println("commit failed!");
//										        	    }
//										            }
//	        		                            });
	        
	        // 异步提交（Lambda 版本）
	        consumer.commitAsync((offsetsMap, exception) -> {
	        										if (exception != null) {
	        											System.out.println("commit failed");
	        										}
	        									});
	    }
	}
}
