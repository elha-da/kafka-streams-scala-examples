package TextStream


import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object TextStream extends App {

  val builder: StreamsBuilder = new StreamsBuilder()

  val streamingConfig = {
    val settings = new Properties
    settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example")
    settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    settings
  }

  // Read input Kafka topic
  val textLines = builder.stream("TextLinesTopic")

  // Write the results to a new Kafka topic
  textLines.to("TextLinesTopicOutPut")

  val stream: KafkaStreams = new KafkaStreams(builder.build(), streamingConfig)
  stream.start()

  sys.ShutdownHookThread {
    stream.close(10, TimeUnit.SECONDS)
  }

}
