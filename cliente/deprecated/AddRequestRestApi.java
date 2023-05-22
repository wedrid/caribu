package com.caribu.cliente;

import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.Router;

public class AddRequestRestApi {
    private static final Logger LOG = LoggerFactory.getLogger(AddRequestRestApi.class);
    //this class is repeated in a way, or rather, it can use another class
    public static void attach(final Router parent) {
        final HashMap<UUID, Richieste> richiestePerRichiedente = new HashMap<UUID, Richieste>();
        final String s = "/richiedente/request/:idRichiedente";
        parent.get(s).handler(context -> {
            var idRichiedente = context.pathParam("accountId");
            LOG.debug("{} for account {}", context.normalizedPath());
            var richieste = richiestePerRichiedente.get(UUID.fromString(idRichiedente));
        });
        parent.put(s).handler(context -> {

        });
        parent.delete(s).handler(context -> {

        });

    }

}
