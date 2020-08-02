package co.com.oscarbustos.sucorrientazoadomicilio.queuemanager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class KafkaManager implements QueueManager {

	private static Logger logger = LogManager.getLogger(KafkaManager.class);

	private static String PRODUCE_TOPIC_NAME;
	private static String CONSUME_TOPIC_NAME;
	private static final String PROPERTIES_PATH = "kafka.properties.path";

	private Producer<Long, String> producer;
	private Consumer<Long, String> consumer;
	private Properties properties;

	public KafkaManager(Path propertiesFile, String produceTopicName, String consumeTopicName) throws IOException {
		loadProperties(propertiesFile);
		PRODUCE_TOPIC_NAME = produceTopicName;
		CONSUME_TOPIC_NAME = consumeTopicName;
	}

	private Producer<Long, String> createProducer() {
		if (producer == null) {
			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
					properties.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
					properties.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
			producer = new KafkaProducer<>(props);
		}
		return producer;
	}

	public void produce(String line) {

		try {
			createTopic(PRODUCE_TOPIC_NAME);
		} catch (Exception e) {
			logger.error("KafkaManager.produce() - Error creating topic " + PRODUCE_TOPIC_NAME, e);
		}

		Producer<Long, String> producer = createProducer();
		System.out.println(PRODUCE_TOPIC_NAME);
		ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(PRODUCE_TOPIC_NAME, line);
		try {
			producer.send(record).get();
		} catch (Exception e) {
			logger.error("KafkaManager.produce() - Error sending the record", e);
		}

	}

	private void createTopic(String topicName) throws Exception {

		AdminClient adminClient = AdminClient.create(properties);
		NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);

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

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Producer<Long, String> getProducer() {
		return producer;
	}

	public void setProducer(Producer<Long, String> producer) {
		this.producer = producer;
	}

	@Override
	public String consume() {
		createConsumer();
		ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
		String line = "";
		if (consumerRecords.records(CONSUME_TOPIC_NAME).iterator() != null
				&& consumerRecords.records(CONSUME_TOPIC_NAME).iterator().hasNext()) {
			ConsumerRecord<Long, String> record = consumerRecords.records(CONSUME_TOPIC_NAME).iterator().next();
			record.offset();
			line = (String) record.value();
		}
		return line;
	}

	public void createConsumer() {
		if (consumer == null) {
			Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
			props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.get(ConsumerConfig.GROUP_ID_CONFIG));

			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					properties.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					properties.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
			props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG));
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
					properties.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
			consumer = new KafkaConsumer<>(props);
			consumer.subscribe(Stream.of(CONSUME_TOPIC_NAME).collect(Collectors.toList()));
		}
	}

	@Override
	public void closeConsumer() {
		consumer.commitSync();
		consumer.close();
	}

	@Override
	public void commit() {
		consumer.commitSync();
	}
	
	
}
