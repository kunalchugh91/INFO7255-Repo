package com.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
//import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

@RestController
public class HomeController {
	
	//@Autowired
	//private NameConverterBean nameConverter ;
	
	@PostMapping("/insert")
	public ResponseEntity<String> insert(@RequestBody String body) {
		if(body == null) {
			return new ResponseEntity<String>("no json body", HttpStatus.BAD_REQUEST);
		}
		
		Schema schema = getSchema();
		if(schema == null)
			return new ResponseEntity<String>("file not found exception", HttpStatus.BAD_REQUEST);
		
		try {
			JSONObject jsonInstance = getJsonObject(body);
			schema.validate(jsonInstance);
			}
		catch (ValidationException e) {
			return new ResponseEntity<String>("invalid", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<String>("valid", HttpStatus.ACCEPTED);
	}
	
	public static Schema getSchema() {
		
	    File file = new File("src/main/resources/schema.json");
	    
	    InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	    
	    JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
	    Schema schema = SchemaLoader.load(rawSchema);
	    return schema;
		
	}
	
	public static JSONObject getJsonObject(String json) {
		
	    return new JSONObject(json);
	}


}
