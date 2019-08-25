name := "kafka-streams-scala-examples"

version := "0.0.1"

scalaVersion := "2.12.8"

val kafkaAvroSerializerV = "5.0.0"

val root = Project("kafka-streams-scala-examples", file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "events-and-bids-to-delivery",
    resolvers ++= Resolvers.base,
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "5.2",

      "org.apache.kafka" % "kafka-streams" % "2.3.0",
//      "org.apache.kafka" %% "kafka-streams-scala" % "2.3.0",
//      "org.apache.kafka" % "kafka-clients" % "2.3.0",
//      "org.apache.avro" % "avro" % "1.9.0",

      "io.confluent" % "kafka-avro-serializer" % kafkaAvroSerializerV,
      "io.confluent" % "kafka-streams-avro-serde" % kafkaAvroSerializerV,
      "com.sksamuel.avro4s" %% "avro4s-core" % "3.0.0",

      "org.codehaus.jackson" % "jackson-core-asl" % "1.9.13",
      "org.codehaus.jackson" % "jackson-mapper-asl" % "1.9.13"
    )
  )
