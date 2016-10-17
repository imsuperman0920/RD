package cn.com.rd.kafka.v0_10_0;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerSample {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		// 连接Kafka服务器
		props.put("bootstrap.servers", "rdmax:9092");
		// ack方式，all，会等所有的commit最慢的方式
		props.put("acks", "all");
		// 失败是否重试，设置会有可能产生重复数据
		props.put("retries", 0);
		// 对于每个partition的batch buffer大小
		props.put("batch.size", 16384);
		// 等多久，如果buffer没满，比如设为1，即消息发送会多1ms的延迟，如果buffer没满
		props.put("linger.ms", 1);
		/*
		 *  整个producer可以用于buffer的内存大小
		 *  producer所能buffer数据的大小，如果数据产生的比发送的快，那么这个buffer会耗尽，因为producer的send的异步的，会先放到buffer，但是如果buffer满了，那么send就会被block，并且当达到max.block.ms时会触发TimeoutException
		 */
		props.put("buffer.memory", 33554432);
		// 最大阻塞时间，设为0则会抛出TimeoutException
		props.put("max.block.ms", 2000);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		Producer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 10; i++) {
			producer.send(new ProducerRecord<String, String>("LOGBMWREQLOG", "key" + Integer.toString(i), "value" + Integer.toString(i)), (metadata, exception) -> {
				if(exception != null) {
					exception.printStackTrace();
				}
			});
		}
		producer.close();
	}
}
