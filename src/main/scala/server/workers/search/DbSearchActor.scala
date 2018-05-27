package server.workers.search

import java.io.File
import akka.actor.{Actor, ActorRef}
import message.{FindBookRequest, SearchResponseNegative, SearchResponsePositive}

import scala.io.Source

class DbSearchActor extends Actor {

  override def receive: Receive = {
    case FindBookRequest(bookTitle, dbID, senderPID, hash) =>
      val file = new File(getClass.getResource(s"/db$dbID/books-db$dbID.txt").toURI)
      val result = Source.fromFile(file).getLines().find(line => line.startsWith(bookTitle))
      result match {
        case Some(data) => sender ! SearchResponsePositive(bookTitle, data.split(" ")(1), senderPID, hash)
        case None => sender ! SearchResponseNegative(senderPID, hash)
      }
  }
}
