package com.matrix.uaas.rest

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait CrudHandler {
  implicit val fixedPath: String
  def composedRoute() = versionOneRoute {
    temperaturePathRoute {
      handleAllMethods()
    }
  }
  private  def versionOneRoute(route: Route) = pathPrefix("v1") {
    route
  }

  private  def temperaturePathRoute(route: Route) = pathPrefix(fixedPath) {
    route
  }

  private  def handleAllMethods() = {
    get {
      handleGet()
    } ~ post {
      handlePost()
    } ~ put {
      handlePut()
    } ~ delete {
      handleDelete()
    }
  }

  def handleGet(): Route
  def handlePut(): Route
  def handlePost(): Route
  def handleDelete(): Route
}
