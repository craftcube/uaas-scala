package com.matrix.uaas

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.{Directive1, Route}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.pattern._

import scala.util.Failure

class HttpServer (host: String, port: Int) extends Actor with ActorLogging

  with JwtHandler {


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

  def apply(host: String, port: Int) = Props(new HttpServer(host, port))

}
