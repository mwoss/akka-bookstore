package server.workers.search

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, Props}
import akka.event.Logging
import message._

import scala.collection.mutable.ArrayBuffer

class SearchActor extends Actor {

  val logger = Logging(context.system, this)

  val dbSearchActor1 = context.actorOf(Props[DbSearchActor], "db_search_actor_1")
  val dbSearchActor2 = context.actorOf(Props[DbSearchActor], "db_search_actor_2")

  var status = new ArrayBuffer[Boolean]()
  var previous = new ArrayBuffer[Boolean]()
  var hash = new AtomicInteger(0)

  override def receive: Receive = {
    case SearchRequest(bookTitle) =>
      val index = hash.getAndIncrement()
      status.insert(index, false)
      previous.insert(index, false)
      dbSearchActor1.tell(FindBookRequest(bookTitle, 1, index), sender)
      dbSearchActor2.tell(FindBookRequest(bookTitle, 2, index), sender)

    case response: SearchResponse =>
      if(!status(response.hash))
        status(response.hash) = true
      else{
        response match {
          case SearchResponsePositive(bookTitle, price, hash) =>
            if (status(hash)) {
              previous(hash) = true
              sender ! BookFound(bookTitle, price)
            }
          case SearchResponseNegative(hash) =>
            if (status(hash) && !previous(hash))
              sender ! BookNotFound
        }
      }

  }
}
