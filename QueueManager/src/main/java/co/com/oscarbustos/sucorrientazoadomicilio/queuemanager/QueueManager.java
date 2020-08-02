package co.com.oscarbustos.sucorrientazoadomicilio.queuemanager;

public interface QueueManager {

	public void produce(String line);
	public String consume();
	public void closeConsumer();
	public void commit();
}
