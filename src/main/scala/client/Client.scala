package client

import java.io.File

import akka.actor.{ActorSystem, Props, ActorRef}
import com.typesafe.config.ConfigFactory
import message.SearchRequest

import scala.io.StdIn.readLine
import scala.util.control.Breaks._

object Client{
  def main(args: Array[String]): Unit = {
    val configFile = new File(getClass.getResource("/client.conf").toURI)
    val config = ConfigFactory.parseFile(configFile)
    val system = ActorSystem.create("client_system", config)

    println("Hi. Can you insert your nickname? :)")
    val clientName = readLine("Your name: ")
    val clientActor = system.actorOf(Props[ClientActor], clientName)

    println("Available options: search [book-name], order [book-name], stream [book-name]")
    breakable {
      while (true) {
        var data: String = readLine()
        data match {
          case req if data.startsWith("search") => clientActor.tell(SearchRequest(data.split(" ")(1)), ActorRef.noSender)
          case req if data.startsWith("order") => println("order")
          case req if data.startsWith("stream") => println("stream")
          case "q" => break
          case _ => println("Unrecognized command")
        }
      }
    }
    system.terminate()
  }
}
