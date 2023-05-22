package com.caribu.cliente;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caribu.cliente.config.DbConfig;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class FlywayMigration {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayMigration.class);

    public static Future<Void> migrate(final Vertx vertx, final DbConfig dbConfig){
        return vertx.<Void>executeBlocking(promise -> {
            execute(dbConfig); // because the content of the method is blocking (uses jdbc) and we want to put it in the non blocking event loop
            promise.complete();
        })
        .onFailure(err -> LOG.error("Error while migrating DB schema with exception error: {}", err));
    }

    private static void execute(final DbConfig dbConfig) {
        final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", 
            dbConfig.getHost(), 
            dbConfig.getPort(), 
            dbConfig.getDatabase());
        LOG.debug("Migraing DB schema using jdbc url: {}", jdbcUrl);
        final Flyway flyway = Flyway.configure()
            .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
            .schemas("client")
            .defaultSchema("client")
            .load();

        var current = Optional.ofNullable(flyway.info().current());
        current.ifPresent(info -> LOG.info("db schema is at version: {}", info.getVersion()));
        var pendingMigrations = flyway.info().pending();
        LOG.debug("Pending migrations: {}", pendingMigrations(pendingMigrations));
        flyway.migrate();
    }

    private static Object pendingMigrations(MigrationInfo[] pending) {
        if (Objects.isNull(pending)){
            return "[]";
        }
        return Arrays.stream(pending)
            .map(each -> each.getVersion() + " - " + each.getDescription())
            .collect(Collectors.joining(", ", "[", "]"));
    }

}
