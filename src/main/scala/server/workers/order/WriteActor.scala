package server.workers.order

import java.io.{File, FileOutputStream, PrintWriter}
import java.nio.file.Paths

import akka.actor.Actor
import message.{OrderSuccess, WriteOrderRequest}

class WriteActor extends Actor{
  val orderPath = "./src/main/resources/orders"

  override def receive: Receive = {
    case WriteOrderRequest(bookTitle) =>
      val orders = new PrintWriter(new FileOutputStream(new File(Paths.get(orderPath).toUri),true))
      orders.append(bookTitle + "\n")
      orders.close()
      sender ! OrderSuccess(bookTitle)
  }

}
