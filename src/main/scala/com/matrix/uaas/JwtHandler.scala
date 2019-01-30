package com.matrix.uaas

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, _}
import com.matrix.uaas.DomainModel.LoginRequest

import scala.util.{Failure, Success}

trait JwtHandler extends UaasJsonSupport{

//  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
//  import io.circe.generic.auto._
//  import authentikat.jwt._
  import pdi.jwt.{Jwt, JwtAlgorithm, JwtHeader, JwtClaim, JwtOptions}
  
  
  


  private val tokenExpiryPeriodInDays = 1
  private val secretKey               = "super_secret_key"
  private val header                  = JwtHeader(JwtAlgorithm.HS256)

  def login = post {
    entity(as[LoginRequest]) {
      case lr @ LoginRequest("admin", "admin") =>
//        val claims = setClaims(lr.username, tokenExpiryPeriodInDays)
//        respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
        val claims = JwtClaim(Map("user" -> lr.username).toString()).expiresIn(300)
        respondWithHeader(RawHeader("Access-Token", Jwt.encode(header, claims, secretKey))) {
          complete(StatusCodes.OK)
        }
      case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
    }
  }

  def securedContent = get {
    authenticated { claims =>
      complete(s"User ${claims.getOrElse("user", "")} accessed secured content!")
    }
  }

  private def authenticated: Directive1[Map[String, Any]] =
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(jwt) if Jwt.isValid(jwt) =>
        Jwt.decode(jwt,secretKey,Seq(JwtAlgorithm.HS256)) match {
          case Success(msg)=>
            complete(msg)
          case Failure(exception: Exception)=>
            println(s"${exception}")
            complete(StatusCodes.Unauthorized)
          case _ =>
            complete(StatusCodes.Unauthorized)
        }

      case _ =>complete(StatusCodes.Unauthorized)
      
      
//      case Some(jwt) if isTokenExpired(jwt) =>
//        complete(StatusCodes.Unauthorized -> "Token expired.")
//
//      case Some(jwt) if JsonWebToken.validate(jwt, secretKey) =>
//        provide(getClaims(jwt).getOrElse(Map.empty[String, Any]))
//
//      case _ => complete(StatusCodes.Unauthorized)
    }

//  private def setClaims(username: String, expiryPeriodInDays: Long) = JwtClaimsSet(
//    Map("user" -> username,
//      "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS
//        .toMillis(expiryPeriodInDays)))
//  )
//
//  private def getClaims(jwt: String) = jwt match {
//    case JsonWebToken(_, claims, _) => claims.asSimpleMap.toOption
//    case _                          => None
//  }
//
//  private def isTokenExpired(jwt: String) = getClaims(jwt) match {
//    case Some(claims) =>
//      claims.get("expiredAt") match {
//        case Some(value) => value.toLong < System.currentTimeMillis()
//        case None        => false
//      }
//    case None => false
//  }

}
