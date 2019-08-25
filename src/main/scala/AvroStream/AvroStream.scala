package AvroStream

import java.util.concurrent.TimeUnit
import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{KStream, Produced}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object AvroStream extends App {

  private val inputTopic = "avro_orders_topic";
  private val outputTopic = "avro_orders_topic-output";
  private val schemaRegistryUrl = "http://localhost:8081";
  private val brokers = "localhost:9092"

  val builder: StreamsBuilder = new StreamsBuilder()

  val getKafkaProperties: Properties = {
    val config: Properties = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "inhabitants-per-city")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[GenericAvroSerde])
    config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
    config
  }

  val orders: KStream[Array[Byte], GenericRecord] = builder.stream(inputTopic)

  orders.to(outputTopic)

  val byteArraySerde: Serde[Array[Byte]] = Serdes.ByteArray()

  val genericAvroSerde: Serde[GenericRecord] = {
    val gas = new GenericAvroSerde

    val isKeySerde: Boolean = false
    gas.configure(Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl), isKeySerde)
    gas
  }
  orders.to(outputTopic, Produced.`with`(byteArraySerde, genericAvroSerde))

  val streams = new KafkaStreams(builder.build(), getKafkaProperties)

  streams.start();

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}
