package server.workers.search

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, Props}
import message._

import scala.collection.mutable.{ArrayBuffer}

class SearchActor extends Actor {

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
      dbSearchActor1 ! FindBookRequest(bookTitle, 1, sender, index)
      dbSearchActor2 ! FindBookRequest(bookTitle, 2, sender, index)
    case response: SearchResponse =>
      if(!status(response.hash))
        status(response.hash) = true
      else{
        response match {
          case SearchResponsePositive(bookTitle, price, senderPID, hash) =>
            if (status(hash)) {
              previous(hash) = true
              senderPID ! BookFound(bookTitle, price)
            }
          case SearchResponseNegative(senderPID, hash) =>
            if (status(hash) && !previous(hash))
              senderPID ! BookNotFound
        }
      }

  }
}
