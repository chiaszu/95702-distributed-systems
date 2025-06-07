import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class RecipeApiClient {
    private static final String API_URL = "https://www.themealdb.com/api/json/v1/1/";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            // Create a scanner for user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter an ingredient to search for recipes: ");
            String ingredient = scanner.nextLine();
            scanner.close();

            // Build the API URL - search by ingredient
            String searchUrl = API_URL + "filter.php?i=" + ingredient.trim();

            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .GET()
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse JSON response with Gson
            MealSearchResponse searchResponse = gson.fromJson(response.body(), MealSearchResponse.class);

            // Check if meals were found
            if (searchResponse != null && searchResponse.meals != null) {
                // Extract and print recipe information
                System.out.println("\nFound " + searchResponse.meals.size() + " recipes with '" + ingredient + "':");

                for (int i = 0; i < searchResponse.meals.size(); i++) {
                    MealSummary meal = searchResponse.meals.get(i);
                    System.out.println("\nRecipe #" + (i+1) + ":");
                    System.out.println("Meal ID: " + meal.idMeal);
                    System.out.println("Name: " + meal.strMeal);
                    System.out.println("Thumbnail URL: " + meal.strMealThumb);
                }

                // Get detailed information for the first recipe
                if (!searchResponse.meals.isEmpty()) {
                    String mealId = searchResponse.meals.get(0).idMeal;
                    getRecipeDetails(mealId, client);
                }
            } else {
                System.out.println("No recipes found with ingredient: " + ingredient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getRecipeDetails(String mealId, HttpClient client) throws Exception {
        // Build the API URL for recipe details
        String detailUrl = API_URL + "lookup.php?i=" + mealId;

        // Create HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(detailUrl))
                .GET()
                .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse JSON response with Gson
        MealDetailResponse detailResponse = gson.fromJson(response.body(), MealDetailResponse.class);

        if (detailResponse != null && !detailResponse.meals.isEmpty()) {
            MealDetail meal = detailResponse.meals.get(0);

            // Print detailed recipe information
            System.out.println("\n===== DETAILED RECIPE INFORMATION =====");
            System.out.println("Name: " + meal.strMeal);
            System.out.println("Category: " + meal.strCategory);
            System.out.println("Area: " + meal.strArea);

            // Print ingredients and measurements
            meal.printIngredients();

            // Print instructions
            System.out.println("\nInstructions:");
            System.out.println(meal.strInstructions);
        }
    }
}