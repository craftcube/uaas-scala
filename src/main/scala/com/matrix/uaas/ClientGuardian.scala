package com.matrix.uaas

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
//import com.matrix.{ClientAssetActor, ClientOrderActor, ClientPositionActor, ClientSettingActor}

object ClientGuardian{
	def props(clientId: String) = Props(new ClientGuardian(clientId))
}

class ClientGuardian(clientId: String) extends Actor with ActorLogging{
	val cluster = Cluster(context.system)
	
	override def preStart(): Unit = {
//		context.actorOf(ClientAssetActor.props(clientId))
//		context.actorOf(ClientOrderActor.props(clientId))
//		context.actorOf(ClientPositionActor.props(clientId))
//		context.actorOf(ClientSettingActor.props(clientId))
	}
	
	override def receive: Receive = {
		case _=>
		
	}
	
}
