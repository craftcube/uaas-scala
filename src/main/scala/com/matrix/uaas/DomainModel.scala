package com.matrix.uaas

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.matrix.uaas.DomainModel.{LogOutRequest, LoginRequest, RegisterRequest, ReturnResult}
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}

object DomainModel {
 
 
 case class LoginRequest(email: String, password: String)
 case class RegisterRequest(email: String, password: String, confirmPass: String)
 case class LogOutRequest(email: String)
 
 case class Client(clientId: String, email: String, password: String, salt: String)
 
 case class ReturnResult(message: String, status: Int)
 
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
 implicit val loginFormat = jsonFormat2(LoginRequest)
 implicit val registerFormat = jsonFormat3(RegisterRequest)
 implicit val logoutFormat = jsonFormat1(LogOutRequest)
 
 implicit val resultFormat=jsonFormat2(ReturnResult)
 
// implicit val createInstrumentFormat = jsonFormat1(CreateInstrumentCmd)
}