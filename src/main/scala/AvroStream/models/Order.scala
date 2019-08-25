package AvroStream.models

import java.io.File

import com.typesafe.scalalogging.StrictLogging
import org.apache.avro.Schema
import org.apache.avro.file.DataFileReader
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.avro.io.DatumReader

case class Order(ordertime: Option[Long], orderid: Option[Int], itemid: Option[String], orderunits: Option[Double], address: Option[Adress])

//object Order extends StrictLogging {
//
//  val datumReader: DatumReader[GenericRecord] = new GenericDatumReader()
//  val dataFileReader: DataFileReader[GenericRecord] = new DataFileReader(new File("file.avro"), datumReader)
//  val schema: Schema = dataFileReader.getSchema()
//
//  logger.info(s"$schema")
//}
