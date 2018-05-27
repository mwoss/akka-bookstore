package server

import akka.actor.{Actor, Props}
import akka.event.Logging
import message.{OrderRequest, SearchRequest, StreamRequest}
import server.workers.order.OrderActor
import server.workers.search.SearchActor
import server.workers.stream.StreamActor

class StoreActor extends Actor {

  val logger = Logging(context.system, this)

  val searchActor = context.actorOf(Props[SearchActor], "search_actor")
  val orderActor = context.actorOf(Props[OrderActor], "order_actor")
  val streamActor = context.actorOf(Props[StreamActor], "stream_actor")

  override def receive: Receive = {
    case request: SearchRequest => searchActor.tell(request, sender)
    case request: OrderRequest => orderActor.tell(request, sender)
    case request: StreamRequest => streamActor.tell(request, sender)
    case _ => println("Received unknown message")
  }
}
