// author: Chia-Szu, Kuo (chiaszuk)

package cmu.edu.recipeideas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private Button searchButton;
    private RadioGroup searchTypeGroup;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noResultsView;
    private RecipeAdapter recipeAdapter;
    private BackgroundTask currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchTypeGroup = findViewById(R.id.searchTypeGroup);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noResultsView = findViewById(R.id.noResultsView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(recipeAdapter);

        // Set up search button click listener
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String searchTerm = searchEditText.getText().toString().trim();

        if (searchTerm.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cancel any ongoing task
        if (currentTask != null) {
            currentTask.cancel(true);
        }

        String searchType = getSelectedSearchType();
        currentTask = new BackgroundTask(this);
        currentTask.execute(searchTerm, searchType);
    }

    private String getSelectedSearchType() {
        int selectedId = searchTypeGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioIngredient) {
            return "ingredient";
        } else if (selectedId == R.id.radioCategory) {
            return "category";
        } else {
            return "name";
        }
    }

    public void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);

        try {
            if (result != null) {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray mealsArray = jsonObject.optJSONArray("recipes");

                if (mealsArray != null && mealsArray.length() > 0) {
                    List<Recipe> recipes = new ArrayList<>();
                    for (int i = 0; i < mealsArray.length(); i++) {
                        JSONObject meal = mealsArray.getJSONObject(i);
                        Recipe recipe = new Recipe(
                                meal.getString("id"),
                                meal.getString("name"),
                                meal.optString("thumbnail")
                        );
                        recipes.add(recipe);
                    }
                    recipeAdapter.setRecipes(recipes);
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultsView.setVisibility(View.GONE);
                } else {
                    showError("No recipes found");
                }
            } else {
                showError("Network error. Please check your connection.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showError("Error processing results");
        }
    }

    private void showError(String message) {
        recyclerView.setVisibility(View.GONE);
        noResultsView.setVisibility(View.VISIBLE);
        noResultsView.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    // Getter for progressBar (needed by BackgroundTask)
    public ProgressBar getProgressBar() {
        return progressBar;
    }
}