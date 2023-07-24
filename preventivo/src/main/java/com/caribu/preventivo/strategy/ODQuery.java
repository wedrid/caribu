package com.caribu.preventivo.strategy;

import java.util.HashMap;
import java.util.Map;

/* ConcreteStrategy */
public class ODQuery implements StrategyQuery {

    @Override
	public String createQuery(Query query) {
		// Stessa origine e destianzione
		Map<String, Object> parameters = new HashMap<>();
                    parameters.put("oLat", query.getOLat());
                    parameters.put("oLon", query.getOLon());
                    parameters.put("dLat", query.getDLat());
                    parameters.put("dLon", query.getDLon());

		String input_Ogeo= "ST_SetSRID(ST_MakePoint(#{oLon} , #{oLat}), 4326)";
		String input_Dgeo= "ST_SetSRID(ST_MakePoint(#{dLon} , #{dLat}), 4326)";
		String query_string= "SELECT * from schema.quotes o where "+ input_Ogeo + "= o.origin_geom AND "+ input_Dgeo + "= o.destination_geom " ;
		return query_string;
	}
}