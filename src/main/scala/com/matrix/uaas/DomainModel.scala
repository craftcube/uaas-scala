package com.matrix.uaas

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.matrix.uaas.DomainModel.LoginRequest
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}

object DomainModel {
 
 final case class LoginRequest(username: String, password: String)
 
}

trait UaasJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
 
 // implicit object ColorJsonFormat extends RootJsonFormat[InstrumentState] {
 //  def write(c: InstrumentState) = JsNumber(c.id)
 //
 //  def read(value: JsValue) = value match {
 //   case JsNumber(blue) => InstrumentState.apply(blue.toInt)
 //   case _=> InstrumentState.delisted
 //  }
 // }
 implicit val instrumentFormat = jsonFormat2(LoginRequest)
// implicit val createInstrumentFormat = jsonFormat1(CreateInstrumentCmd)
}