package net.c5h8no4na.e621.api;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.c5h8no4na.common.network.ApiResponse;
import net.c5h8no4na.e621.api.response.FullUserApi;
import net.c5h8no4na.e621.api.response.PoolApi;
import net.c5h8no4na.e621.api.response.PostApi;
import net.c5h8no4na.e621.api.response.TagApi;

class E621ClientTest {

	private static E621Client client;

	@BeforeAll
	static void prepare() {
		client = new E621Client("earlopain(test-suite)");
	}

	@Test
	void testApiResponse() {
		ApiResponse<PostApi> exists = client.getPost(100);
		ApiResponse<PostApi> notExists = client.getPost(1);
		assertSuccessfulResponse(exists);
		assertErrorResponse(notExists);
	}

	@Test
	void testGetPostById() {
		ApiResponse<PostApi> exists = client.getPost(100);
		ApiResponse<PostApi> notExists = client.getPost(1);

		// Test response status
		assertSuccessfulResponse(exists);
		assertErrorResponse(notExists);

		// Test response content
		PostApi post = exists.unwrap();
		Assertions.assertEquals(100, post.getId());
		Assertions.assertEquals("d85c7d365cf593cae3cdf7c931325ee7", post.getFile().getMd5());
	}

	@Test
	void testGetMultiplePosts() {
		ApiResponse<List<PostApi>> response1 = client.getPosts();
		ApiResponse<List<PostApi>> response2 = client.getPosts(-1, 1, 100);
		ApiResponse<List<PostApi>> response3 = client.getPosts(1, 2, 5, 100, 500, 700, 1000, 5000, 7500, 10000);
		assertSuccessfulResponse(response1);
		assertSuccessfulResponse(response2);
		assertSuccessfulResponse(response3);

		// Test empty list
		Assertions.assertEquals(0, response1.unwrap().size());
		// Test list with deleted post
		Assertions.assertEquals(1, response2.unwrap().size());
		// Test large list
		Assertions.assertEquals(5, response3.unwrap().size());
	}

	@Test
	void testGetTagById() {
		ApiResponse<TagApi> dragon = client.getTagById(1);
		ApiResponse<TagApi> notExists1 = client.getTagById(0);
		ApiResponse<TagApi> notExists2 = client.getTagById(-1);

		assertSuccessfulResponse(dragon);
		assertErrorResponse(notExists1);
		assertErrorResponse(notExists2);

		TagApi tag = dragon.unwrap();

		Assertions.assertEquals(1, tag.getId());
		Assertions.assertEquals("dragon", tag.getName());
		Assertions.assertEquals(5, tag.getCategory());
	}

	@Test
	void testGetMultipleTags() {
		ApiResponse<List<TagApi>> response1 = client.getTagsByName();
		ApiResponse<List<TagApi>> response2 = client.getTagsByName("ghhehehe", "adas", "male");
		ApiResponse<List<TagApi>> response3 = client.getTagsByName("male", "female", "dragon", "pokémon");
		assertSuccessfulResponse(response1);
		assertSuccessfulResponse(response2);
		assertSuccessfulResponse(response3);
		// If not names are passed api returns the last 75
		Assertions.assertEquals(75, response1.unwrap().size());
		Assertions.assertEquals(1, response2.unwrap().size());
		Assertions.assertEquals(4, response3.unwrap().size());
	}

	@Test
	void testGetTagByName() {
		ApiResponse<TagApi> dragon = client.getTagByName("dragon");
		ApiResponse<TagApi> pokemon = client.getTagByName("pokémon");
		ApiResponse<TagApi> integerTag = client.getTagByName("123");
		ApiResponse<TagApi> nonExists = client.getTagByName("wgmwpood");
		ApiResponse<TagApi> notExistsIntegerTag = client.getTagByName("12398252");

		assertSuccessfulResponse(dragon);
		assertSuccessfulResponse(pokemon);
		assertSuccessfulResponse(integerTag);
		assertErrorResponse(nonExists);
		assertErrorResponse(notExistsIntegerTag);

		Assertions.assertEquals(1, dragon.unwrap().getId());
		Assertions.assertEquals(16913, pokemon.unwrap().getId());
		Assertions.assertEquals(181869, integerTag.unwrap().getId());
		Assertions.assertEquals("123", integerTag.unwrap().getName());
	}

	@Test
	void testGetUserById() {
		ApiResponse<FullUserApi> existingUser = client.getUserById(194340);
		ApiResponse<FullUserApi> notExists = client.getUserById(0);
		assertSuccessfulResponse(existingUser);
		assertErrorResponse(notExists);
		FullUserApi earlopain = existingUser.unwrap();
		Assertions.assertEquals("Earlopain", earlopain.getName());
		Assertions.assertEquals(194340, earlopain.getId());

	}

	@Test
	void testGetUserByName() {
		ApiResponse<FullUserApi> existingUser = client.getUserByName("earlopain");
		ApiResponse<FullUserApi> userWithIntegerName = client.getUserByName("123");
		ApiResponse<FullUserApi> nonExistingUser = client.getUserByName("jfowjfofwf");
		ApiResponse<FullUserApi> nonExistingUserWithIntegers = client.getUserByName("80895019751");

		assertSuccessfulResponse(existingUser);
		assertSuccessfulResponse(userWithIntegerName);
		assertErrorResponse(nonExistingUser);
		assertErrorResponse(nonExistingUserWithIntegers);

		FullUserApi earlopain = existingUser.unwrap();
		FullUserApi integerUser = userWithIntegerName.unwrap();

		Assertions.assertEquals(194340, earlopain.getId());
		Assertions.assertEquals(3288, integerUser.getId());
		Assertions.assertEquals("123", integerUser.getName());
	}

	@Test
	void testGetPoolById() {
		ApiResponse<PoolApi> existingPool = client.getPoolById(17319);
		ApiResponse<PoolApi> nonExistantPool = client.getPoolById(-1);

		assertSuccessfulResponse(existingPool);
		assertErrorResponse(nonExistantPool);

		PoolApi pool = existingPool.unwrap();

		Assertions.assertEquals(17319, pool.getId());
		Assertions.assertEquals(169756, pool.getCreatorId());
		Assertions.assertEquals("Caelum_Sky", pool.getName());
	}

	@Test
	void testPostHasValues() {
		PostApi post = client.getPost(2597886).unwrap();
		Assertions.assertTrue(post.getFile().getUrl().isPresent());
		Assertions.assertTrue(post.getPreview().getUrl().isPresent());
		Assertions.assertTrue(post.getSample().getUrl().isPresent());
		Assertions.assertTrue(post.getDuration().isPresent());
		Assertions.assertEquals(3, post.getSample().getAlternates().size());
		Assertions.assertEquals(2, post.getSample().getAlternates().get("480p").getUrls().size());
		Assertions.assertEquals(38571, post.getApproverId().get());

		PostApi post2 = client.getPost(2165995).unwrap();
		Assertions.assertTrue(post2.getFile().getUrl().isEmpty());
		Assertions.assertTrue(post2.getPreview().getUrl().isEmpty());
		Assertions.assertTrue(post2.getSample().getUrl().isEmpty());
		Assertions.assertTrue(post2.getDuration().isEmpty());
		Assertions.assertEquals(0, post2.getSample().getAlternates().size());
		Assertions.assertEquals(169756, post2.getApproverId().get());

	}

	@SuppressWarnings("rawtypes")
	private void assertSuccessfulResponse(ApiResponse r) {
		Assertions.assertTrue(r.isSuccess());
		Assertions.assertNull(r.getErrorType());
		Assertions.assertNull(r.getErrorMessage());
		Assertions.assertTrue(r.getResponseCode() >= 200 && r.getResponseCode() < 300);
		Assertions.assertDoesNotThrow(() -> r.unwrap());
	}

	@SuppressWarnings("rawtypes")
	private void assertErrorResponse(ApiResponse r) {
		Assertions.assertFalse(r.isSuccess());
		Assertions.assertNull(r.getErrorType());
		Assertions.assertNotNull(r.getErrorMessage());
		Assertions.assertThrows(AssertionError.class, () -> r.unwrap());
	}
}
