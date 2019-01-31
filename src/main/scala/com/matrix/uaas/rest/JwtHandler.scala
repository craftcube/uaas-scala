package com.matrix.uaas.rest

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.matrix.uaas.DomainModel.{LoginRequest, RegisterRequest, ReturnResult}
import com.matrix.uaas.UaasJsonSupport
import scala.concurrent.duration._
import akka.pattern._

import scala.util.{Failure, Success}


object JwtHandler extends UaasJsonSupport {
	
	
	import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader}
	
	implicit val timeout: Timeout = Timeout(5 second)
	
	
	//  private val tokenExpiryPeriodInDays = 1
	private val secretKey = "super_secret_key"
	private val header = JwtHeader(JwtAlgorithm.HS256)
	
	
	def composedRoute(clientShardActor:ActorRef) = versionOneRoute {
		temperaturePathRoute {
			handleAllMethods(clientShardActor)
		}
	}
	
	private def versionOneRoute(route: Route) = pathPrefix("v1") {
		route
	}
	
	private def temperaturePathRoute(route: Route) = pathPrefix("user") {
		route
	}
	
	private def handleAllMethods(clientShardActor:ActorRef) = {

		login(clientShardActor) ~ logout(clientShardActor) ~ register(clientShardActor) ~hi
//		hi
		
	}
	
	private def register(clientShardActor:ActorRef) = pathPrefix("register") {
		post {
			entity(as[RegisterRequest]) { content=>
				println("=========================")
				
					if(content.email.isEmpty)
						complete(ReturnResult("email is empty!",-1))
					if(content.confirmPass!=content.password || content.password.isEmpty)
						complete(ReturnResult("password is wrong!",-1))
					else
						complete((clientShardActor ? content).mapTo[ReturnResult])
				
				
				
//				case lr@LoginRequest("admin", "admin") =>
//					//        val claims = setClaims(lr.username, tokenExpiryPeriodInDays)
//					//        respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
//					val claims = JwtClaim(Map("user" -> lr.email).toString()).expiresIn(300)
//					respondWithHeader(RawHeader("Access-Token", Jwt.encode(header, claims, secretKey))) {
//						complete(StatusCodes.OK)
//					}
//				case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
			}
		}
	}
	
	
	private def login(clientShardActor:ActorRef) = pathPrefix("login") {
		post {
			entity(as[LoginRequest]) {
				case lr@LoginRequest("admin", "admin") =>
					//        val claims = setClaims(lr.username, tokenExpiryPeriodInDays)
					//        respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
					val claims = JwtClaim(Map("user" -> lr.email).toString()).expiresIn(300)
					respondWithHeader(RawHeader("Access-Token", Jwt.encode(header, claims, secretKey))) {
						complete(StatusCodes.OK)
					}
				case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
			}
		}
	}
	private def logout(clientShardActor:ActorRef) = pathPrefix("logout") {
		post {
			entity(as[LoginRequest]) {
				case lr@LoginRequest("admin", "admin") =>
					//        val claims = setClaims(lr.username, tokenExpiryPeriodInDays)
					//        respondWithHeader(RawHeader("Access-Token", JsonWebToken(header, claims, secretKey))) {
					val claims = JwtClaim(Map("user" -> lr.email).toString()).expiresIn(300)
					respondWithHeader(RawHeader("Access-Token", Jwt.encode(header, claims, secretKey))) {
						complete(StatusCodes.OK)
					}
				case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
			}
		}
	}
	
	private def hi = get {
			complete(s"hi!")
	}
	
	def securedContent = get {
		authenticated { claims =>
			complete(s"User ${claims.getOrElse("user", "")} accessed secured content!")
		}
	}
	
	private def authenticated: Directive1[Map[String, Any]] =
		optionalHeaderValueByName("Authorization").flatMap {
			case Some(jwt) if Jwt.isValid(jwt) =>
				Jwt.decode(jwt, secretKey, Seq(JwtAlgorithm.HS256)) match {
					case Success(msg) =>
						complete(msg)
					case Failure(exception: Exception) =>
						println(s"${exception}")
						complete(StatusCodes.Unauthorized)
					case _ =>
						complete(StatusCodes.Unauthorized)
				}
			
			case _ => complete(StatusCodes.Unauthorized)
			
			
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
