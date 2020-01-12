import java.util.ArrayList;

/*
 * Class: Recipe
 * Entity class for storing recipe information
 */
public class Recipe {

    private long id;
    private String name;
    private String instructions;
    private String ingredients;
    private int duration;
    private ArrayList<Integer> categories;


    /*
     * Constructor for uninitialized recipes
     */
    public Recipe(){
        this.name="";
        this.instructions = "";
        this.ingredients = "";
        this.duration = -1;
        this.categories = new ArrayList<>();
        this.id = 0;
    }

    /*
     * Regular constructor
     */

    public Recipe(long id_par, String name_par, String instructions_par, String ingredients_par, int duration, ArrayList<Integer> categories_par){
        this.name=name_par;
        this.instructions = instructions_par;
        this.ingredients = ingredients_par;
        this.duration = duration;
        this.categories = categories_par;
        this.id = id_par;
    }



    /*
     * Getters and setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getDuration(){return this.duration;}

    public ArrayList<Integer> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Integer> categories) {
        this.categories = categories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
