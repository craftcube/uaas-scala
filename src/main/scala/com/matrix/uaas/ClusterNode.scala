package com.matrix.uaas

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.management.AkkaManagement
import akka.management.cluster.bootstrap.ClusterBootstrap
import com.typesafe.config.ConfigFactory

class ClusterNode(nr: Int) {
	
	val config = ConfigFactory.parseString(
		s"""
      akka.remote.artery.canonical.hostname = "127.0.0.$nr"
      akka.management.http.hostname = "127.0.0.$nr"
    """).withFallback(ConfigFactory.load("application.conf"))
	val system = ActorSystem("matrix-uaas", config)
	
	AkkaManagement(system).start()
	
	ClusterBootstrap(system).start()
	
	Cluster(system).registerOnMemberUp({
		system.log.info("Cluster is up!")
		
		
		
		//		system.actorOf(ClusterSingletonManager.props(Props[ExchangeManager], PoisonPill,
		//			ClusterSingletonManagerSettings(system)), "exchangeMgr")
		//
		//		val exchangeMgr = system.actorOf(ClusterSingletonProxy.props(
		//			singletonManagerPath = "/user/exchangeMgr",
		//			settings = ClusterSingletonProxySettings(system)),
		//			name = "exchangeMgrProxy")
		//
		//		val meShardActor: ActorRef = ClusterSharding(system).start(
		//			typeName = MatchEngineShardActor.shardName,
		//			entityProps = Props[MatchEngineShardActor],
		//			settings = ClusterShardingSettings(system),
		//			extractEntityId = MatchEngineShardActor.extractEntityId,
		//			extractShardId = MatchEngineShardActor.extractShardId)
		
		val clientShardActor: ActorRef = ClusterSharding(system).start(
			typeName = ClientShardActor.shardName,
			entityProps = Props[ClientShardActor],
			settings = ClusterShardingSettings(system),
			extractEntityId = ClientShardActor.extractEntityId,
			extractShardId = ClientShardActor.extractShardId)
		
		system.actorOf(HttpServer.apply(s"127.0.0.$nr", 8090, clientShardActor))
	})
}
