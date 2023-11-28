package com.caribu.preventivo.strategy;

/* ConcreteStrategy */
public class GenericQuery implements StrategyQuery {

	@Override
	public String createQuery(Query query) {
		String query_string = "SELECT *, ST_DistanceSphere(o.origin_geom, o.destination_geom) as dist, 0 as dist_in from schema.quotes o WHERE"
				+ query.getCost();
		return query_string;
	}
}