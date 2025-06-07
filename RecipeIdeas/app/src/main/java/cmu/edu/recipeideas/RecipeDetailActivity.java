// author: Chia-Szu, Kuo (chiaszuk)

package cmu.edu.recipeideas;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RecipeDetailActivity extends AppCompatActivity {
    private ImageView recipeImageView;
    private TextView recipeTitleView;
    private TextView recipeIngredientsView;
    private TextView recipeInstructionsView;
    private ProgressBar progressBar;
    private AsyncTask<String, Void, String> currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize views
        recipeImageView = findViewById(R.id.detailRecipeImage);
        recipeTitleView = findViewById(R.id.detailRecipeTitle);
        recipeIngredientsView = findViewById(R.id.detailRecipeIngredients);
        recipeInstructionsView = findViewById(R.id.detailRecipeInstructions);
        progressBar = findViewById(R.id.detailProgressBar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get recipe info from intent
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");

        if (recipeId == null || recipeName == null) {
            Toast.makeText(this, "Error: Recipe information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set title and image
        recipeTitleView.setText(recipeName);
        if (recipeImage != null && !recipeImage.isEmpty()) {
            Picasso.get()
                .load(recipeImage)
                .error(R.drawable.ic_no_results)
                .into(recipeImageView);
        }

        // Fetch recipe details
        fetchRecipeDetails(recipeId);
    }

    private void fetchRecipeDetails(String recipeId) {
        currentTask = new AsyncTask<String, Void, String>() {
            private Exception exception;

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                recipeIngredientsView.setText("");
                recipeInstructionsView.setText("");
            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    String webServiceUrl = "https://ominous-space-xylophone-6pj965wpgvvc77g-8080.app.github.dev/";
                    String urlString = String.format("%s/recipe/search?term=%s&type=id",
                            webServiceUrl, URLEncoder.encode(params[0], "UTF-8"));

                    System.out.println("Fetching recipe details from: " + urlString);

                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);

                    int responseCode = connection.getResponseCode();
                    System.out.println("Recipe detail response code: " + responseCode);

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        // Read error stream if available
                        BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String errorLine;
                        while ((errorLine = errorReader.readLine()) != null) {
                            errorResponse.append(errorLine);
                        }
                        errorReader.close();
                        System.out.println("Error response: " + errorResponse.toString());
                        throw new Exception("Server returned code: " + responseCode);
                    }

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return response.toString();

                } catch (Exception e) {
                    this.exception = e;
                    return null;
                } finally {
                    try {
                        if (reader != null) reader.close();
                        if (connection != null) connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onPostExecute(String result) {
                progressBar.setVisibility(View.GONE);

                if (result == null) {
                    String errorMsg = exception != null ? exception.getMessage() : "Unknown error";
                    Toast.makeText(RecipeDetailActivity.this,
                        "Error loading recipe: " + errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray recipesArray = jsonObject.optJSONArray("recipes");

                    if (recipesArray == null || recipesArray.length() == 0) {
                        Toast.makeText(RecipeDetailActivity.this,
                                "Recipe details not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject recipe = recipesArray.getJSONObject(0);

                    // Build ingredients list
                    StringBuilder ingredients = new StringBuilder();
                    JSONArray ingredientsArray = recipe.optJSONArray("ingredients");
                    JSONArray measurementsArray = recipe.optJSONArray("measurements");

                    if (ingredientsArray != null && measurementsArray != null) {
                        for (int i = 0; i < ingredientsArray.length(); i++) {
                            String ingredient = ingredientsArray.optString(i);
                            String measure = measurementsArray.optString(i);

                            if (ingredient != null && !ingredient.trim().isEmpty()
                                && !ingredient.equals("null")) {
                                ingredients.append("• ");
                                if (measure != null && !measure.trim().isEmpty()
                                    && !measure.equals("null")) {
                                    ingredients.append(measure.trim()).append(" ");
                                }
                                ingredients.append(ingredient.trim()).append("\n");
                            }
                        }
                    }

                    String instructions = recipe.optString("instructions", "No instructions available");

                    recipeIngredientsView.setText(ingredients.toString().trim());
                    recipeInstructionsView.setText(instructions.trim());

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RecipeDetailActivity.this,
                        "Error processing recipe data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(recipeId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}