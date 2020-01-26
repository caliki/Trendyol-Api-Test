package tech.trendyol;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class ODMBApiTest {
	private String apiKey = "8410c58e";

	@Before
	public void before() {
		RestAssured.baseURI = "http://www.omdbapi.com?";
	}

	private Movie findMovieBySearchWordAndTitle(String searchWord, String title) {
		RequestSpecification httpReq = given().queryParam("apiKey", apiKey).queryParam("s", searchWord);
		Response response = httpReq.get();
		JsonPath jsonPathEvaluator = response.jsonPath();
		List<Movie> foundedMovies = jsonPathEvaluator.getList("Search", Movie.class);
		for (Movie movie : foundedMovies) {
			if (movie.Title.equals(title)) {
				return movie;
			}
		}
		return null;
	}

	@Test
	public void testOMDBApiConsistency() {
		String searchWord = "Harry Potter";
		String title = "Harry Potter and the Sorcerer's Stone";
		Movie foundedMovie = findMovieBySearchWordAndTitle(searchWord, title);
		Assert.assertNotNull(foundedMovie);
		given()
			.param("apiKey", apiKey)
			.param("i", foundedMovie.imdbID)
			.when()
			.get()
			.then()
			.statusCode(200)
			.body("Title", equalTo(foundedMovie.Title))
			.body("Year", equalTo(foundedMovie.Year))
			.body("Released", notNullValue());
	}
}
