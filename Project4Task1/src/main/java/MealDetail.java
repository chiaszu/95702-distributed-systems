public class MealDetail {
    String idMeal;
    String strMeal;
    String strCategory;
    String strArea;
    String strInstructions;
    String strMealThumb;

    // Ingredients
    String strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5;
    String strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10;
    String strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15;
    String strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20;

    // Measurements
    String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5;
    String strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10;
    String strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15;
    String strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;

    public void printIngredients() {
        String[] ingredients = {
                strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
                strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
                strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
                strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        };

        String[] measures = {
                strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
                strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
                strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
                strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
        };

        System.out.println("\nIngredients:");
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i] != null && !ingredients[i].trim().isEmpty()) {
                String measure = measures[i] != null ? measures[i].trim() : "";
                System.out.println("- " + measure + " " + ingredients[i].trim());
            }
        }
    }
}