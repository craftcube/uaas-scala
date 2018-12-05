package com.matrix.uaas

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Boot {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem()
    implicit val materializer = ActorMaterializer()

    system.actorOf(HttpServer.apply("localhost", 8000), HttpServer.name)
  }
}
