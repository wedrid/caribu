package com.caribu.preventivo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.preventivo.config.ConfigLoader;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;



public class VersionInfoVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VersionInfoVerticle.class);

  @Override
  public Completable rxStart() {
      // Load the configuration
      return ConfigLoader.load(vertx)
        // Log the current application version when the configuration is successfully loaded
        .doOnSuccess(configuration -> LOG.info("Current Application Version is: {}", configuration.getVersion()))
        // Log an error message if the configuration fails to load
        .doOnError(throwable -> LOG.error("Failed to load configuration", throwable))
        .ignoreElement(); // ignoreElement returns Completable. Otherwise it returned single. 
    }
}
