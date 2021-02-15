package net.c5h8no4na.e621.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EndpointTest {

	@Test
	void testEndpointGet() {
		String url = "/posts.json";
		Assertions.assertEquals(Endpoint.POSTS.get().toString(), url);
		Assertions.assertNotEquals(Endpoint.POSTS.get().toString(), Endpoint.TAGS.get().toString());
	}

	@Test
	void testEndpointById() {
		String url = "/posts/123.json";
		Assertions.assertEquals(Endpoint.POSTS.getById(123).toString(), url);
		Assertions.assertNotEquals(Endpoint.POSTS.getById(1).toString(), Endpoint.POSTS.getById(2).toString());
		Assertions.assertNotEquals(Endpoint.POSTS.getById(1).toString(), Endpoint.TAGS.getById(1).toString());
	}

	@Test
	void testEndpointByString() {
		String url1 = "/posts/test.json";
		String url2 = "/posts/pok%C3%A9mon.json";
		Assertions.assertEquals(Endpoint.POSTS.getByString("test").toString(), url1);
		Assertions.assertEquals(Endpoint.POSTS.getByString("pokémon").toString(), url2);
		Assertions.assertNotEquals(Endpoint.POSTS.getByString("a").toString(), Endpoint.POSTS.getByString("b").toString());
		Assertions.assertNotEquals(Endpoint.POSTS.getByString("a").toString(), Endpoint.TAGS.getByString("a").toString());
	}

	@Test
	void testEndpointWithParams() {
		String urlNoParams = "/posts.json";
		String urlWithParams = "/posts.json?param1=value1&param2=%E3%82%B4%E3%82%B4&param3=under_scores_%28brackets%29";
		Map<String, String> queryParams1 = new LinkedHashMap<>();
		queryParams1.put("param1", "value1");
		queryParams1.put("param2", "ゴゴ");
		queryParams1.put("param3", "under_scores_(brackets)");
		Map<String, String> queryParams2 = new LinkedHashMap<>();
		queryParams2.put("param1", "value1");
		String buildNoParams = Endpoint.POSTS.getWithParams(Collections.emptyMap());
		String buildWithParams = Endpoint.POSTS.getWithParams(queryParams1);

		Assertions.assertEquals(buildNoParams, urlNoParams);
		Assertions.assertEquals(buildWithParams, urlWithParams);
		Assertions.assertNotEquals(Endpoint.POSTS.getWithParams(queryParams1).toString(),
				Endpoint.POSTS.getWithParams(queryParams2).toString());
		Assertions.assertNotEquals(Endpoint.POSTS.getWithParams(queryParams1).toString(),
				Endpoint.TAGS.getWithParams(queryParams1).toString());
	}

}
