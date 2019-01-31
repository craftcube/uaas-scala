package com.matrix.uaas

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.ShardRegion
import com.matrix.uaas.ClientShardActor.Order
import com.matrix.uaas.DomainModel.{LogOutRequest, LoginRequest, RegisterRequest, ReturnResult}
//import com.matrix.ClientActor

object ClientShardActor {
	
	//	case class Login(userId: String, userName: String, password : String)
	//	case class GetClientData(userId: String)
	//	case class FreezePosition()
	case class Order(instrumentId: String, clientId: String, limit: Double)
	
	//cluster sharding
	val extractEntityId: ShardRegion.ExtractEntityId = {
		//		case msg@StartMatchEngine(instrument) ⇒ (s"${instrument.instrumentId}", msg)
		case msg@Order(instrumentId, clientId, limit) ⇒ (s"${clientId}", msg)
		case msg@LoginRequest(email, password) => (s"${email.hashCode}", msg)
		case msg@LogOutRequest(email) => (s"${email.hashCode}", msg)
		case msg@RegisterRequest(email, password, confirmPass) => (s"${email.hashCode}", msg)
	}
	
	val numberOfShards = 4
	
	val extractShardId: ShardRegion.ExtractShardId = {
		//    case StartMatchEngine(instrument) ⇒ (s"${instrument.instrumentId}".hashCode % numberOfShards).toString
		//    case MatchOrder(order) ⇒ (s"${order.instrumentId}".hashCode % numberOfShards).toString
		//		case StartMatchEngine(instrument) ⇒ (s"${instrument.instrumentId}").toString
		case Order(_, clientId, _) ⇒ (s"${clientId}").toString
		case LoginRequest(email, password) => s"${email.hashCode}".toString
		case LogOutRequest(email) => s"${email.hashCode}".toString
		case RegisterRequest(email, password, confirmPass) => s"${email.hashCode}".toString
	}
	
	val shardName = "clientShardActor"
	
	//	def props(clientManager: ActorRef) = Props(new ClientMonitor(clientManager))
}


class ClientShardActor() extends Actor with ActorLogging {
	
	override def preStart(): Unit = {
		
		println(s"ClientMonitor have been created at ${cluster.selfUniqueAddress}")
	}
	
	val cluster = Cluster(context.system)
	
	var clientMap: Map[String, ActorRef] = Map()
	
	override def receive: Receive = {
		case Order(_, clientId, _) =>
			if (!clientMap.contains(clientId)) {
				
				cluster.system.actorOf(Props[ClientGuardian])
				
				val clientInstance = context.actorOf(Props[ClientGuardian])
				context.watch(clientInstance)
				clientMap += (clientId -> clientInstance)
			}
		
		case LoginRequest(email, password) =>
		case LogOutRequest(email) =>
		case RegisterRequest(email, password, confirmPass) =>


			sender()! ReturnResult("user created!",0)
	}
}

