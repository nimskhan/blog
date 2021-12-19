package blog;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class VerifyBlogData {
	
	final String BASE_URL = "https://jsonplaceholder.typicode.com";
	
	@Test
	public void SearchFoUserWithUsername() {

		String username = GetUser("Delphine");

		Assert.assertTrue(username != "");

		if (username != "") {
			System.out.println("The user with the Username \"Delphine\" is: " + username);
		} else {
			System.out.println("The user with the Username \"Delphine\" not found.");
		}

	}
	
	@Test
	public void SearchForThePostsWrittenByTheUser() {

        String posts = GetPosts(GetUser("Delphine")).toString();
		Assert.assertTrue(posts != "");

		if (posts != "") {
			System.out.println("The posts with the Username \"Delphine\" are: " + posts);
		} else {
			System.out.println("The user with the Username \"Delphine\" are not found.");
		}

	}
	
	@Test
	public void ForEachPostFetchTheCommentsAndValidateIfTheEmailsInTheCommentSectionAreInTheProperFormat() {
		
		System.out.println("The results if the email address against the comments of Username \"Delphine\" are following:");
		
		/*
		 * Regular Expression: "^(.+)@(.+)$"
		 * That only checks for the ‘@’ in an email address— the
		 * other conditions for a valid email address will not be checked; and there can
		 * be any number of characters before and after the sign.
		 */
		VerifyEmailOfEachComments(GetPosts(GetUser("Delphine")));
	}

	
	public void GetUserAndVerifyEmailAgainstEachComment() {
				
		        System.out.println("User is:"+GetUser("Delphine"));
				System.out.println("Posts are:"+GetPosts(GetUser("Delphine")));
				VerifyEmailOfEachComments(GetPosts(GetUser("Delphine")));
				
	}
	
	public String GetUser(String username) {
		        // Specify the base URL to the RESTful web service
				RestAssured.baseURI = BASE_URL;

				// Get the RequestSpecification of the request that you want to sent
				// to the server. The server is specified by the BaseURI that we have
				// specified in the above step.
				RequestSpecification httpRequest = RestAssured.given();

				// Make a GET request call directly by using RequestSpecification.get() method.
				Response response = httpRequest.get("/users?username="+username);

				// Response.asString method will directly return the content of the body
				JsonPath jsonPathEvaluator = response.jsonPath();
				
				// Read all the users as a List of String. Each item in the list
				List<Integer> allusers =  jsonPathEvaluator.getList("id");

				// Iterate over the list and print individual book item
				String useris="";
				for(Integer user : allusers)
				{
					useris= user.toString();
				}
				
				return useris.toString();
				
	}
	
	public List<Integer> GetPosts(String userId) {
		        // Specify the base URL to the RESTful web service
				RestAssured.baseURI = BASE_URL;

				// Get the RequestSpecification of the request that you want to sent
				RequestSpecification httpRequest = RestAssured.given();

				// Make a GET request call directly by using RequestSpecification.get() method.
				Response response = httpRequest.get("/posts?userId="+userId);

				// Response.asString method will directly return the content of the body
				
				/// First get the JsonPath object instance from the Response interface
				JsonPath jsonPathEvaluator = response.jsonPath();
				
				// Read all the users as a List of String. Each item in the list
				List<Integer> allposts =  jsonPathEvaluator.getList("id");
				
				return allposts;
				
	}
	
	public void VerifyEmailOfEachComments(List<Integer> posts) {

		// Iterate over the list and print individual book item
		for (Integer postId : posts) {
			// Specify the base URL to the RESTful web service
			RestAssured.baseURI = BASE_URL;

			// Get the RequestSpecification of the request that you want to sent
			RequestSpecification httpRequest = RestAssured.given();

			// Make a GET request call directly by using RequestSpecification.get() method.
			Response response = httpRequest.get("/comments?postId=" + postId);

			// Response.asString method will directly return the content of the body
			/// First get the JsonPath object instance from the Response interface
			JsonPath jsonPathEvaluator = response.jsonPath();

			// Read all the books as a List of String. Each item in the list
			// represent a post node in the REST service Response
			List<Integer> allcomments = jsonPathEvaluator.getList("id");

			for (Integer comment : allcomments) {

				RequestSpecification commenthttpRequest = RestAssured.given();
				Response commentresponse = commenthttpRequest.get("/comments?id=" + comment);

				JsonPath commentJsonPathEvaluator = commentresponse.jsonPath();
				String email = commentJsonPathEvaluator.getString("email");

				/*
				 * Regular Expression: That only checks for the ‘@’ in an email address— the
				 * other conditions for a valid email address will not be checked; and there can
				 * be any number of characters before and after the sign.
				 */
				String regex = "^(.+)@(.+)$";
				// Compile regular expression to get the pattern
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(email);
				System.out.println("the post id is: " + postId + " the comment" + comment + " email is: " + email
						+ " : " + matcher.matches() + "\n");

			}

		}
	}
	
}
