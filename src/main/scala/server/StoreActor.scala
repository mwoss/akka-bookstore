package server

import akka.actor.{Actor, Props}
import akka.event.Logging
import message.SearchRequest
import server.workers.search.SearchActor

class StoreActor extends Actor {

  val logger = Logging(context.system, this)
  val searchActor = context.actorOf(Props[SearchActor], "search_actor")

  override def receive: Receive = {
    case request: SearchRequest => searchActor.tell(request, sender)
    case _ => println("dunno how to match ")
  }
}
