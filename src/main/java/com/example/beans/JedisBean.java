package com.example.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class JedisBean {

	private static final String redisHost = "localhost";
	private static final Integer redisPort = 6379;
	private static JedisPool pool = null;
	
	public JedisBean() {
		pool = new JedisPool(redisHost, redisPort);
	}
	
	public UUID insert(JSONObject jsonObject) {
		UUID idOne = UUID.randomUUID();
		if(insertUtil(jsonObject, idOne))
			return idOne;
		else
			return null;
	}
	
	private boolean insertUtil(JSONObject jsonObject, UUID uuid) {
		
		try {
			Jedis jedis = pool.getResource();
			
			for(Object key : jsonObject.keySet()) {
				String attributeKey = uuid + "_" + String.valueOf(key);
				Object attributeVal = jsonObject.get(String.valueOf(key));
				
				if(attributeVal instanceof JSONObject) {
					Map<String,String> map = handleObjectAsMap( (JSONObject) attributeVal);
					jedis.hmset(attributeKey, map);
				} else if (attributeVal instanceof JSONArray) {
					Set<String> set  = handleObjectAsArray((JSONArray) attributeVal);
					for(String v : set)
						jedis.sadd(attributeKey, v);
				} else {
					jedis.set(attributeKey, String.valueOf(attributeVal));
				}
			}
		}
		catch(JedisException e) {
			return false;
		}
		
		return true;
	}
	
	private Map<String,String> handleObjectAsMap(JSONObject jsonObject) {
		Map<String,String> map = new HashMap<String, String>();
		for(String key : jsonObject.keySet()) {
			map.put(key, jsonObject.get(key).toString());
		}
		return map;
	}
	
	private Set<String> handleObjectAsArray(JSONArray jsonArray) {
		Set<String> set = new HashSet<String>();
		for(Object o : jsonArray) {
			JSONObject ob = (JSONObject) o;
			set.add(ob.toString());
		}
		return set;
	}
	
	public boolean delete(String uuid) {
		try {
			Jedis jedis = pool.getResource();
			Set<String> keys = jedis.keys(uuid+"*");
			for(String key : keys) {
				jedis.del(key);
			}
			return true;
		} catch(JedisException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}	

