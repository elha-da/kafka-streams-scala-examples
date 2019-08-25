package AvroStream

import java.util.Properties
import java.util.concurrent.TimeUnit

import models.Order
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

import scala.collection.JavaConverters._

object AvroStreamSpecific {

  /*
  * WIP
  */

  private val inputTopic = "avro_orders_topic";
  private val outputTopic = "avro_orders_topic-output";
  private val schemaRegistryUrl = "http://localhost:8081";
  private val brokers = "localhost:9092"

  def specificAvroSerde[T <: SpecificRecord](schemaRegistry: String, isKey: Boolean): SpecificAvroSerde[T] = {
    val serde = new SpecificAvroSerde[T]()
    serde.configure(Map(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> schemaRegistry).asJava, isKey)
    serde
  }

  val builder: StreamsBuilder = new StreamsBuilder()

  val getKafkaProperties: Properties = {
    val config: Properties = new Properties()
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "inhabitants-per-city")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
    //    config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl)
    //    config.put("processing.guarantee", "exactly_once")
    config
  }

  val byteArraySerde: Serde[Array[Byte]] = Serdes.ByteArray()
  val orderAvroSerde: SpecificAvroSerde[Order] = specificAvroSerde[Order](schemaRegistryUrl, false)

  val orders: KStream[Array[Byte], Order] = builder.stream(inputTopic, Consumed.`with`(byteArraySerde, orderAvroSerde))

  orders.to(outputTopic, Produced.`with`(byteArraySerde, orderAvroSerde))

  val streams = new KafkaStreams(builder.build(), getKafkaProperties)

  streams.start();

  sys.ShutdownHookThread {
    streams.close(10, TimeUnit.SECONDS)
  }

}
