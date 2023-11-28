package com.caribu.preventivo.strategy;

/* ConcreteStrategy */
public class DistanceQuery implements StrategyQuery {
    @Override
	public String createQuery(Query query){
                // FIRENZE -> ROMA
                // oLat=43.7792500&oLon=11.2462600&dLat=41.8919300&dLon=12.5113300
                // FIRENZE->PISA
                // oLat=43.7792500&oLon=11.2462600&dLat=43.7085300&dLon=10.4036000
                
                // Create origin and destination geometry points
                String input_Ogeo = "ST_SetSRID(ST_MakePoint(#{oLon} , #{oLat}), 4326)";
                String input_Dgeo = "ST_SetSRID(ST_MakePoint(#{dLon} , #{dLat}), 4326)";
                String distance = "ST_DistanceSphere(" + input_Ogeo + "," + input_Dgeo + ")";
                String query_string = "SELECT *, ST_DistanceSphere(o.origin_geom, o.destination_geom) as dist, " + distance
                        + " as dist_in from schema.quotes o where ST_DistanceSphere(o.origin_geom, o.destination_geom) BETWEEN "
                        + distance + "- #{soglia} AND " + distance + "+ #{soglia}";

                return query_string;
	}
}
