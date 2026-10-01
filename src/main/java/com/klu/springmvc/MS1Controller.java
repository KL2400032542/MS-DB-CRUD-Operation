package com.klu.springmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klu.springmvc.service.JWTService;

@RestController
@RequestMapping("/ms1")
public class MS1Controller {

	@Autowired
	JWTService jwtService;

	// users kept in memory: username -> password, and username -> id
	private final Map<String, String> passwords = new ConcurrentHashMap<>();
	private final Map<String, Integer> ids = new ConcurrentHashMap<>();
	private final AtomicInteger nextId = new AtomicInteger(1);

	public MS1Controller() {
		// default user, so you can sign in without signing up first
		passwords.put("rama", "1234");
		ids.put("rama", nextId.getAndIncrement());
	}

	@GetMapping("/add")
	public String add(@RequestParam("a") int a, @RequestParam("b") int b) {
		return "MS 1.1 - Addition = " + (a + b);
	}

	@PostMapping("/signup")
	public Object signup(@RequestBody Map<String, String> body) {
		Map<String, Object> response = new HashMap<>();
		String username = body.get("username");
		if (username == null || passwords.containsKey(username)) {
			response.put("code", 500);
			response.put("message", "User already exists or username missing");
			return response;
		}
		passwords.put(username, body.get("password"));
		ids.put(username, nextId.getAndIncrement());
		response.put("code", 200);
		response.put("message", "Signup successful");
		return response;
	}

	@PostMapping("/signin")
	public Object signin(@RequestBody Map<String, String> body) {
		Map<String, Object> response = new HashMap<>();
		String username = body.get("username");
		String password = passwords.get(username);
		if (password == null || !password.equals(body.get("password"))) {
			response.put("code", 500);
			response.put("message", "Invalid username or password");
			return response;
		}
		String token = jwtService.generateJWT(body, "1", ids.get(username));
		response.put("code", 200);
		response.put("token", token);
		return response;
	}
}