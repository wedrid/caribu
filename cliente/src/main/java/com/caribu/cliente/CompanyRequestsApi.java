package com.caribu.cliente;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.ThreadLocalRandom;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;


public class CompanyRequestsApi {
    private static final Logger LOG = LoggerFactory.getLogger(CompanyRequestsApi.class);

    public static void attach(Router parent){
        final Map<String, CompanyRequests> cachedCompanyRequests = new HashMap<>();
        CompaniesRestApi.COMPANIES.forEach(symbol -> 
            cachedCompanyRequests.put(symbol, initFakeCompanyRequests(symbol))
        );

        parent.get("/richiedenti/:azienda").handler(context -> {
            
            final String companyParam = context.pathParam("azienda");
            LOG.debug("Asset parameter: {}", companyParam);
            
            var maybeRequests = Optional.ofNullable(cachedCompanyRequests.get(companyParam));
            
            if (maybeRequests.isEmpty()) {
                context.response()
                    .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                    .end(new JsonObject()
                        .put("message", "quote for asset" + companyParam + " not found")
                        .put("path", context.normalizedPath())
                        .toBuffer()
                    );
                return;
            }

            final JsonObject response = maybeRequests.get().toJsonObject();
            LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
            context.response().end(response.toBuffer());
        });
    }

    private static CompanyRequests initFakeCompanyRequests(String nomeAzienda) {
        return CompanyRequests.builder()
            .nomeAzienda(nomeAzienda)
            .request1("Richiesta 1 di " + nomeAzienda)
            .request2("Richiesta 2 di " + nomeAzienda)
            .numRequests(randomValue())
            .build();
    }

    private static BigDecimal randomValue(){
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }

}
