package com.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
//import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beans.JedisBean;
import com.example.beans.MyJsonValidator;

import redis.clients.jedis.JedisPool;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

@RestController
public class HomeController {
	
	@Autowired
	private MyJsonValidator validator;
	@Autowired
	private JedisBean jedisBean;
	
	
	@RequestMapping("/")
	public String home() {
		return "Welcome!";
	}

	// to read json instance from redis
	@GetMapping("/read/{id}")
	public ResponseEntity<String> read(@PathVariable(name="id", required=true) String id) {
		return new ResponseEntity<String>("invalid", HttpStatus.BAD_REQUEST);
	}
	
	
	// to insert new json instance into redis
	@PostMapping("/insert")
	public ResponseEntity<String> insert(@RequestBody(required=true) String body) {
		
		Schema schema = validator.getSchema();
		if(schema == null)
			return new ResponseEntity<String>("schema file not found exception", HttpStatus.BAD_REQUEST);
		
		JSONObject jsonObject = validator.getJsonObjectFromString(body);
		
		if(validator.validate(jsonObject)) {
			UUID uuid = jedisBean.insert(jsonObject);
			return new ResponseEntity<String>("Inserted with id "+uuid, HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<String>("invalid", HttpStatus.BAD_REQUEST);
		}
			
	}
	
	
	// to delete json instance with key id from redis
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable(name="id", required=true) String id) {
		return new ResponseEntity<String>("invalid", HttpStatus.BAD_REQUEST);
	}
	
	
	// to update json instance with key id in redis
	@PutMapping("/update/{id}")
	public ResponseEntity<String> update(@PathVariable(name="id", required=true) String id, @RequestBody(required=true) String body) {
		
		//if id does not exist
		if(id)
		return insert(body);
		
		//else
		Schema schema = validator.getSchema();
		if(schema == null)
			return new ResponseEntity<String>("schema file not found exception", HttpStatus.BAD_REQUEST);
		
		JSONObject jsonObject = validator.getJsonObjectFromString(body);
		
		if(validator.validate(jsonObject))
			return new ResponseEntity<String>("valid", HttpStatus.ACCEPTED);
		
		else
			return new ResponseEntity<String>("invalid", HttpStatus.BAD_REQUEST);
	}


}
