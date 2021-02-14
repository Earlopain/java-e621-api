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

public class E621ClientTest {

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
		Assertions.assertEquals(post.getId(), 100);
		Assertions.assertEquals(post.getFile().getMd5(), "d85c7d365cf593cae3cdf7c931325ee7");
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
		Assertions.assertEquals(response1.unwrap().size(), 0);
		// Test list with deleted post
		Assertions.assertEquals(response2.unwrap().size(), 1);
		// Test large list
		Assertions.assertEquals(response3.unwrap().size(), 5);
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

		Assertions.assertEquals(tag.getId(), 1);
		Assertions.assertEquals(tag.getName(), "dragon");
		Assertions.assertEquals(tag.getCategory(), 5);
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
		Assertions.assertEquals(response1.unwrap().size(), 75);
		Assertions.assertEquals(response2.unwrap().size(), 1);
		Assertions.assertEquals(response3.unwrap().size(), 4);
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

		Assertions.assertEquals(dragon.unwrap().getId(), 1);
		Assertions.assertEquals(pokemon.unwrap().getId(), 16913);
		Assertions.assertEquals(integerTag.unwrap().getId(), 181869);
		Assertions.assertEquals(integerTag.unwrap().getName(), "123");
	}

	@Test
	void testGetUserById() {
		ApiResponse<FullUserApi> existingUser = client.getUserById(194340);
		ApiResponse<FullUserApi> notExists = client.getUserById(0);
		assertSuccessfulResponse(existingUser);
		assertErrorResponse(notExists);
		FullUserApi earlopain = existingUser.unwrap();
		Assertions.assertEquals(earlopain.getName(), "Earlopain");
		Assertions.assertEquals(earlopain.getId(), 194340);

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

		Assertions.assertEquals(earlopain.getId(), 194340);
		Assertions.assertEquals(integerUser.getId(), 3288);
		Assertions.assertEquals(integerUser.getName(), "123");
	}

	@Test
	void testGetPoolById() {
		ApiResponse<PoolApi> existingPool = client.getPoolById(17319);
		ApiResponse<PoolApi> nonExistantPool = client.getPoolById(-1);

		assertSuccessfulResponse(existingPool);
		assertErrorResponse(nonExistantPool);

		PoolApi pool = existingPool.unwrap();

		Assertions.assertEquals(pool.getId(), 17319);
		Assertions.assertEquals(pool.getCreatorId(), 169756);
		Assertions.assertEquals(pool.getName(), "Caelum_Sky");
	}

	@Test
	void testPostHasValues() {
		PostApi post = client.getPost(2597886).unwrap();
		Assertions.assertTrue(post.getFile().getUrl().isPresent());
		Assertions.assertTrue(post.getPreview().getUrl().isPresent());
		Assertions.assertTrue(post.getSample().getUrl().isPresent());
		Assertions.assertTrue(post.getSample().getAlternates().size() == 3);
		Assertions.assertTrue(post.getSample().getAlternates().get("480p").getUrls().size() == 2);
		Assertions.assertTrue(post.getApproverId().isPresent() && post.getApproverId().get() == 38571);
		Assertions.assertTrue(post.getDuration().isPresent());

		PostApi post2 = client.getPost(2165995).unwrap();
		Assertions.assertTrue(post2.getFile().getUrl().isEmpty());
		Assertions.assertTrue(post2.getPreview().getUrl().isEmpty());
		Assertions.assertTrue(post2.getSample().getUrl().isEmpty());
		Assertions.assertTrue(post2.getSample().getAlternates().size() == 0);
		Assertions.assertTrue(post2.getApproverId().isPresent() && post2.getApproverId().get() == 169756);
		Assertions.assertTrue(post2.getDuration().isEmpty());

	}

	@SuppressWarnings("rawtypes")
	private void assertSuccessfulResponse(ApiResponse r) {
		Assertions.assertTrue(r.getSuccess());
		Assertions.assertNull(r.getErrorType());
		Assertions.assertNull(r.getErrorMessage());
		Assertions.assertTrue(r.getResponseCode() >= 200 && r.getResponseCode() < 300);
		Assertions.assertDoesNotThrow(() -> r.unwrap());
	}

	@SuppressWarnings("rawtypes")
	private void assertErrorResponse(ApiResponse r) {
		Assertions.assertFalse(r.getSuccess());
		Assertions.assertNull(r.getErrorType());
		Assertions.assertNotNull(r.getErrorMessage());
		Assertions.assertThrows(AssertionError.class, () -> r.unwrap());
	}
}
