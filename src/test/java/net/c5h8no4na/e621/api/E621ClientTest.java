package net.c5h8no4na.e621.api;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.c5h8no4na.common.network.ApiResponse;
import net.c5h8no4na.e621.api.response.Post;
import net.c5h8no4na.e621.api.response.Tag;

public class E621ClientTest {

	private static E621Client client;

	@BeforeAll
	static void prepare() {
		client = new E621Client("earlopain(test-suite)");
	}

	@Test
	void testApiResponse() {
		ApiResponse<Post> exists = client.getPost(100);
		ApiResponse<Post> notExists = client.getPost(1);
		assertSuccessfulResponse(exists);
		assertErrorResponse(notExists);
	}

	@Test
	void testGetPostById() {
		ApiResponse<Post> exists = client.getPost(100);
		ApiResponse<Post> notExists = client.getPost(1);

		// Test response status
		assertSuccessfulResponse(exists);
		assertErrorResponse(notExists);

		// Test response content
		Post post = exists.unwrap();
		Assertions.assertEquals(post.getId(), 100);
		Assertions.assertEquals(post.getFile().getMd5(), "d85c7d365cf593cae3cdf7c931325ee7");
	}

	@Test
	void testGetMultiplePosts() {
		ApiResponse<List<Post>> response1 = client.getPosts();
		ApiResponse<List<Post>> response2 = client.getPosts(-1, 1, 100);
		ApiResponse<List<Post>> response3 = client.getPosts(1, 2, 5, 100, 500, 700, 1000, 5000, 7500, 10000);
		assertSuccessfulResponse(response1, response2, response3);

		// Test empty list
		Assertions.assertEquals(response1.unwrap().size(), 0);
		// Test list with deleted post
		Assertions.assertEquals(response2.unwrap().size(), 1);
		// Test large list
		Assertions.assertEquals(response3.unwrap().size(), 5);
	}

	@Test
	void testGetTagById() {
		ApiResponse<Tag> dragon = client.getTagById(1);
		ApiResponse<Tag> notExists1 = client.getTagById(0);
		ApiResponse<Tag> notExists2 = client.getTagById(-1);

		assertSuccessfulResponse(dragon);
		assertErrorResponse(notExists1, notExists2);

		Tag tag = dragon.unwrap();

		Assertions.assertEquals(tag.getId(), 1);
		Assertions.assertEquals(tag.getName(), "dragon");
		Assertions.assertEquals(tag.getCategory(), 5);
	}

	@Test
	void testGetMultipleTags() {
		ApiResponse<List<Tag>> response1 = client.getTagsByName();
		ApiResponse<List<Tag>> response2 = client.getTagsByName("ghhehehe", "adas", "male");
		ApiResponse<List<Tag>> response3 = client.getTagsByName("male", "female", "dragon", "pokémon");
		assertSuccessfulResponse(response1, response2, response3);
		// If not names are passed api returns the last 75
		Assertions.assertEquals(response1.unwrap().size(), 75);
		Assertions.assertEquals(response2.unwrap().size(), 1);
		Assertions.assertEquals(response3.unwrap().size(), 4);
	}

	@Test
	void testGetTagByName() {
		ApiResponse<Tag> dragon = client.getTagByName("dragon");
		ApiResponse<Tag> pokemon = client.getTagByName("pokémon");
		ApiResponse<Tag> nonExists = client.getTagByName("wgmwpood");
		assertSuccessfulResponse(dragon, pokemon);
		assertErrorResponse(nonExists);

		Assertions.assertEquals(dragon.unwrap().getId(), 1);
		Assertions.assertEquals(pokemon.unwrap().getId(), 16913);
	}

	@SuppressWarnings("rawtypes")
	private void assertSuccessfulResponse(ApiResponse... responses) {
		for (ApiResponse r : responses) {
			Assertions.assertTrue(r.getSuccess());
			Assertions.assertNull(r.getErrorType());
			Assertions.assertNull(r.getErrorMessage());
			Assertions.assertTrue(r.getResponseCode() >= 200 && r.getResponseCode() < 300);
			Assertions.assertDoesNotThrow(() -> r.unwrap());
		}
	}

	@SuppressWarnings("rawtypes")
	private void assertErrorResponse(ApiResponse... responses) {
		for (ApiResponse r : responses) {
			Assertions.assertFalse(r.getSuccess());
			Assertions.assertNull(r.getErrorType());
			Assertions.assertNotNull(r.getErrorMessage());
			Assertions.assertThrows(AssertionError.class, () -> r.unwrap());
		}
	}
}
