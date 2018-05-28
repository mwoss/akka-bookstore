package server.workers.order

import akka.pattern.ask
import akka.actor.{Actor, Props}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}
import message._

import scala.concurrent.ExecutionContextExecutor

class OrderActor extends Actor {

  implicit val duration: Timeout = 10 seconds
  private implicit val executionContext: ExecutionContextExecutor = context.system.dispatcher

  val searchActor = context.actorSelection("/user/store_actor/search_actor")
  val writeActor = context.actorOf(Props[WriteActor], "write_actor")


  override def receive: Receive = {
    case request: OrderRequest =>
      val client = sender
      searchActor.ask(SearchRequest(request.bookTitle)).onComplete {
        case Success(status) =>
          status match {
            case BookFound(bookTitle, _) => writeActor.tell(WriteOrderRequest(bookTitle), client)
            case BookNotFound => client ! OrderFailure
          }
        case Failure(_) => client ! OrderFailure
      }
  }
}
