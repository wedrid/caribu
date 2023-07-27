package com.caribu.preventivo.strategy;

import java.util.HashMap;
import java.util.Map;

/* ConcreteStrategy */
public class ODQuery implements StrategyQuery {

	@Override
	public String createQuery(Query query) {
		// Stessa origine e destianzione
		String input_Ogeo = "ST_SetSRID(ST_MakePoint(#{oLon} , #{oLat}), 4326)";
		String input_Dgeo = "ST_SetSRID(ST_MakePoint(#{dLon} , #{dLat}), 4326)";
		String query_string = "SELECT *, ST_DistanceSphere(o.origin_geom, o.destination_geom) as dist, ST_DistanceSphere("
				+ input_Ogeo + ", " + input_Dgeo
				+ ") as dist_in from schema.quotes o where ST_DistanceSphere(o.origin_geom, " + input_Ogeo
				+ ") <20000 AND ST_DistanceSphere(o.destination_geom, " + input_Dgeo + ") <20000";
		return query_string;
	}
}