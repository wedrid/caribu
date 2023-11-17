package com.caribu.prova_event_bus;

import java.util.Properties;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
      System.out.println("MainVerticle started!");
      vertx.eventBus().consumer("added-tratta-address", (message) -> {
          JsonObject body = (JsonObject) message.body();
          System.out.println(body.toString());
      }); 
  }


  public static void main(String[] args) {
    // WARNING: [192.168.64.1]:5703 [dev] [4.2.8] Config seed port is 5701 and cluster size is 1. Some of the ports seem occupied!


    // ClusterManager mgr = new HazelcastClusterManager();
    

    //Prova con specifica delle porte
    /*Config hazelcastConfig = new Config();
    NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();
    networkConfig.setPort(5701);
    networkConfig.setPortCount(100);
    networkConfig.setPortAutoIncrement(true);

    HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
    ClusterManager mgr = new HazelcastClusterManager(hazelcastInstance);

    */    

    Config hazelcastConfig = new Config();
    hazelcastConfig.getNetworkConfig().setPort(6000) // Set the initial port
              .setPortAutoIncrement(true);
    
    NetworkConfig networkConfig = hazelcastConfig.getNetworkConfig();

    JoinConfig joinConfig = networkConfig.getJoin();
    joinConfig.getMulticastConfig().setEnabled(false);
    joinConfig.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");
    
    // some configuration settings
    ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
    VertxOptions options = new VertxOptions().setClusterManager(mgr);
    // Deploy verticle
    Vertx
      .clusteredVertx(options, cluster -> {
       if (cluster.succeeded()) {
           cluster.result().deployVerticle(new MainVerticle(), res -> {
               if(res.succeeded()){
                  System.out.println("Deployment id is: " + res.result());
               } else {
                  System.out.println("Deployment failed!");
               }
           });
       } else {
           System.out.println("Cluster up failed: " + cluster.cause());
       }
    });
  }
}
