package server.workers.search

import java.io.File
import akka.actor.Actor
import message.{FindBookRequest, SearchResponseNegative, SearchResponsePositive}

import scala.io.Source

class DbSearchActor extends Actor {

  override def receive: Receive = {
    case FindBookRequest(bookTitle, dbID, hash) =>
      val file = new File(getClass.getResource(s"/db$dbID/books-db$dbID.txt").toURI)
      val result = Source.fromFile(file).getLines().find(line => line.startsWith(bookTitle))
      result match {
        case Some(data) => context.parent.tell(SearchResponsePositive(bookTitle, data.split(" ")(1), hash), sender)
        case None => context.parent.tell(SearchResponseNegative(hash), sender)
      }
  }
}
