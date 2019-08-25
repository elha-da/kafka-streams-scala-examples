import sbt.{Resolver, _}

object Resolvers {

  val base = Seq(
    Resolver.jcenterRepo,
    "confluent maven repo" at "http://packages.confluent.io/maven/"
  )

}

