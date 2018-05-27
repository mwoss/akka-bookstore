package message

import akka.actor.ActorRef

case class SearchRequest(bookTitle: String) extends Serializable
case class OrderRequest(bookTitle: String) extends Serializable
case class StreamRequest(bookTitle: String) extends Serializable

sealed trait SearchResponse{
  val hash: Int
}
case class SearchResponsePositive(bookTitle: String, price: String, senderPID: ActorRef, hash: Int) extends SearchResponse with Serializable
case class SearchResponseNegative(senderPID: ActorRef, hash: Int) extends SearchResponse with Serializable

case class FindBookRequest(bookTitle: String, dbID: Int, senderPID: ActorRef, hash: Int) extends Serializable

case class BookFound(bookTitle: String, price: String) extends Serializable
case class BookNotFound() extends Serializable