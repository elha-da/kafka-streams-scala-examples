package TextStream

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{Consumed, KStream}
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}


object TextStreamUpper extends App with StrictLogging {

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

  val uppercasedWithMapValues: KStream[Array[Byte], String] = textLines.mapValues { v => v.toUpperCase() }

  // Write the results to a new Kafka topic
  uppercasedWithMapValues.to("UppercasedTextLinesTopic")

  // using map
  val uppercasedWithMap: KStream[Array[Byte], String] = textLines.map {
    (key, value) =>
      logger.info(s"$value")
//      KeyValue.pair(key, value.toUpperCase())
      new KeyValue(key, value.toUpperCase())
  }

  uppercasedWithMap.to("UppercasedTextLinesTopic")

  val stream: KafkaStreams = new KafkaStreams(builder.build(), streamingConfig)
  stream.start()

  sys.ShutdownHookThread {
    stream.close(10, TimeUnit.SECONDS)
  }

}
