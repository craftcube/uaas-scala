package com.matrix.uaas

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.pattern._

import scala.util.Failure

class HttpServer (host: String, port: Int, clientShardActor: ActorRef) extends Actor with ActorLogging

  with JwtHandler {
  
  val cluster = Cluster(context.system)


  import context.dispatcher

  private implicit val materializer = ActorMaterializer()


  val routes: Route =  login ~ securedContent

  Http(context.system).bindAndHandle(routes, host, port).pipeTo(self)

  override def receive: Receive = {
    case ServerBinding(address) =>
      log.info("Server successfully bound at {}:{}", address.getHostName, address.getPort)
    case Failure(cause) =>
      log.error("Failed to bind server", cause)
      context.system.terminate()
  }
}


object HttpServer{
  val name="http-server"

  def apply(host: String, port: Int, clientShardActor: ActorRef) = Props(new HttpServer(host, port, clientShardActor))

}
