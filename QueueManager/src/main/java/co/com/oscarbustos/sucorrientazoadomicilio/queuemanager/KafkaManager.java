package co.com.oscarbustos.sucorrientazoadomicilio.queuemanager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class KafkaManager implements QueueManager {

	private static Logger logger = LogManager.getLogger(KafkaManager.class);

	private static String TOPIC_NAME = "topic.name";
	private static String PROPERTIES_PATH = "kafka.properties.path";

	private static Producer<Long, String> producer;
	private static Properties properties;

	public KafkaManager(Path propertiesFile, String topicName) throws IOException {
		loadProperties(propertiesFile);
		TOPIC_NAME = topicName;
	}

	private static Producer<Long, String> createProducer() {
		if (producer == null) {
			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
			// props.put(ProducerConfig.CLIENT_ID_CONFIG,
			// properties.get(ProducerConfig.CLIENT_ID_CONFIG));
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					properties.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					properties.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
			producer = new KafkaProducer<>(props);
		}
		return producer;
	}

	public static void main(String args[]) throws IOException {
		if (args[0] != null) {
			Path propertiesFile = new File(args[0]).toPath();
			if (propertiesFile.toFile().exists()) {
				KafkaManager k = new KafkaManager(propertiesFile, "in01");
				k.produce("new message 01");
			}
		}
	}

	public void produce(String line) {

		try {
			createTopic();
		} catch (Exception e) {
			logger.error("KafkaManager.produce() - Error creating topic " + TOPIC_NAME, e);
		}

		Producer<Long, String> producer = createProducer();

		ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(TOPIC_NAME, line);
		try {
			producer.send(record).get();
		} catch (Exception e) {
			logger.error("KafkaManager.produce() - Error sending the record", e);
		}

	}

	private void createTopic() throws Exception {

		AdminClient adminClient = AdminClient.create(properties);
		NewTopic newTopic = new NewTopic(TOPIC_NAME, 1, (short) 1);

		List<NewTopic> newTopics = new ArrayList<NewTopic>();
		newTopics.add(newTopic);

		adminClient.createTopics(newTopics);
		adminClient.close();
	}

	public void loadProperties(Path propertiesFile) throws IOException {
		try {
			FileReader reader = new FileReader(propertiesFile.toFile());
			Properties props = new Properties();
			props.load(reader);
			reader.close();
			File propsFile = new File(props.getProperty(PROPERTIES_PATH));
			reader = new FileReader(propsFile);

			properties = new Properties();
			properties.load(reader);
			reader.close();

		} catch (IOException e) {
			throw new IOException(
					"KafkaManager.loadProperties(): Error loading properties file " + propertiesFile.getFileName(), e);
		}
	}

	/*
	 * public static Consumer<Long, String> createConsumer() { Properties props =
	 * new Properties(); props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
	 * IKafkaConstants.KAFKA_BROKERS); props.put(ConsumerConfig.GROUP_ID_CONFIG,
	 * IKafkaConstants.GROUP_ID_CONFIG);
	 * props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
	 * LongDeserializer.class.getName());
	 * props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	 * StringDeserializer.class.getName());
	 * props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
	 * IKafkaConstants.MAX_POLL_RECORDS);
	 * props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
	 * props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
	 * IKafkaConstants.OFFSET_RESET_EARLIER); Consumer<Long, String> consumer = new
	 * KafkaConsumer<>(props);
	 * consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));
	 * return consumer; }
	 */

}
