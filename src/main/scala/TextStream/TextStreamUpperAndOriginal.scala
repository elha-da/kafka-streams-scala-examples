package TextStream

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}

object TextStreamUpperAndOriginal extends App {


  val builder: StreamsBuilder = new StreamsBuilder()

  val streamingConfig = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray.getClass.getName)
    settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    settings
  }

  // Read the input Kafka topic
  val textLines: KStream[Array[Byte], String] = builder.stream("TextLinesTopic")

  // using map: modify both key and value
  val originalAndUppercased: KStream[String, String] = textLines.map((key, value) => KeyValue.pair(value, value.toUpperCase()))

  // Write the results to a new Kafka topic
  val stringSerde: Serde[String] = Serdes.String()
  originalAndUppercased.to("OriginalAndUppercasedTopic", Produced.`with`(stringSerde, stringSerde))


  val stream: KafkaStreams = new KafkaStreams(builder.build(), streamingConfig)
  stream.start()

  sys.ShutdownHookThread {
    stream.close(10, TimeUnit.SECONDS)
  }

}