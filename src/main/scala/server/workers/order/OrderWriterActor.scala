package server.workers.order

import java.io.{File, FileWriter}
import java.nio.file.Paths

import akka.actor.Actor
import message.{OrderSuccess, WriteOrderRequest}

class OrderWriterActor extends Actor{

  val orderPath = "./src/main/resources/orders.txt"
  val orders = new FileWriter(new File(Paths.get(orderPath).toUri), true)

  override def receive: Receive = {
    case WriteOrderRequest(bookTitle) =>
      orders.write(bookTitle)
      orders.close()
      sender ! OrderSuccess(bookTitle)
  }
}
