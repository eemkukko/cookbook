import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

/*
 * Class: CookBookController
 * CookBookController includes methods for reading and writing into the save file, as well as sorting recipe ArrayLists
 * The save file is currently a CSV file, although because everything is being done without a CSV parser,
 * it might as well be a TXT file. Perhaps it is at least easier to migrate into a more sophisticated database
 * in the future.
 *
 * This class does not make any changes to the GUI, so it may be wise to skip reading it.
 *
 */
public class CookBookController {

    private static String fileName = "recipes.csv";

    /*
     * Method: SortAZ
     * Sorts the given list of recipes alphabetically by their name
     * Parameter recipes: List to be sorted
     * Parameter ascending: If true, sorts A-Z, if false, sorts Z-A
     */
    public static void sortAZ(ArrayList<Recipe> recipes, boolean ascending){
        if(ascending)
            Collections.sort(recipes,(a,b) -> a.getName().compareToIgnoreCase(b.getName()));
        else
            Collections.sort(recipes,(a,b) -> a.getName().compareToIgnoreCase(b.getName())*-1);
    }
    /*
     * Method: SortDuration
     * Sorts the given list of recipes by their duration
     * Parameter recipes: List to be sorted
     * Parameter ascending: Whether to sort in an ascending order
     */
    public static void sortDuration(ArrayList<Recipe> recipes, boolean ascending){

        //Removing & storing durationless recipes
        ArrayList<Recipe> durationlessRecipes= new ArrayList<>();

        for(Iterator<Recipe> iterator = recipes.iterator();iterator.hasNext();) {
            Recipe r = iterator.next();
            if(r.getDuration()<0) {
                durationlessRecipes.add(r);
                iterator.remove();
            }
        }

        if(ascending)
            Collections.sort(recipes,(a,b) -> a.getDuration() < b.getDuration() ? -1 : a.getDuration() ==  b.getDuration() ? 0 : 1 );
        else
            Collections.sort(recipes,(a,b) -> a.getDuration() > b.getDuration() ? -1 : a.getDuration() ==  b.getDuration() ? 0 : 1 );

        //Adding durationless recipes to the end of the list
        for(Recipe r : durationlessRecipes){
            recipes.add(r);
        }
    }
    /*
     * Method: newRecipeToFile
     * Writes a new recipe to the file. If the file does not exist, a new one is created and
     * headers added.
     * Parameter r: Recipe to be added
     */
    public static void newRecipeToFile(Recipe r){

        File tmpFile = new File(fileName);
        boolean didExist = (tmpFile.isFile());


        try( BufferedWriter dataLogger = new BufferedWriter( new FileWriter(fileName,true))){

            //In case recipes.csv did not exits, creating headers for the rest of the the file.
            if(!didExist)
                dataLogger.write("ID;Name;Instructions;Ingredients;Duration;Categories;\n");

            //Writing recipe data
            dataLogger.write(generateRecipeString(r));

        }
        catch( IOException ioe ){
            System.out.println("IO exception.");
            ioe.printStackTrace();
        }

    }

    /*
    * Method: updateRecipeFile
    * Updates a recipe in file to its new values
    * Parameter modifiedRecipe: Recipe that was modified
    */
    public static void updateRecipeFile(Recipe modifiedRecipe){


        ArrayList<Recipe> recipes = new ArrayList<>();

        boolean recipeFound = false;
        recipes = readRecipes();

        for(Recipe r : recipes ) {
            if (r.getId()==modifiedRecipe.getId()){
                recipeFound = true;
                recipes.set(recipes.indexOf(r),modifiedRecipe);
                break; //No reason to stay in loop once recipe found
            }
        }

        if(!recipeFound) {
            System.out.println("Tried to modify a recipe that does not exist");
            return;
        }

        File recipeFile = new File(fileName);
        recipeFile.delete();

            for (Recipe r : recipes)
                newRecipeToFile(r);


        }

    /*
     * Method: deleteRecipe
     * Deletes a recipe from the file.
     */
    public static void deleteRecipe(Recipe recipeToDelete){

        ArrayList<Recipe> recipes = readRecipes();

        for(Recipe r : recipes){
            if(r.getId()==recipeToDelete.getId()) {
                recipes.remove(r);
                break;
            }
        }


        File recipeFile = new File(fileName);
        recipeFile.delete();

        for (Recipe r : recipes)
            newRecipeToFile(r);
    }

    /*
    * Method: readRecipes
    * Reads a given amount of recipes from file, starting from startID.
    * Returns: The resulting list of recipes.
    */
    public static ArrayList<Recipe> readRecipes(int amount, long startId) {

        ArrayList<Recipe> recipes = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(fileName))){
            scanner.useDelimiter(";");

        if(!advanceScannerToRecipe(scanner,startId)){
            System.out.println("Recipe not found with ID: " + startId);
            return recipes;
        }

        recipes.add(scanRecipeAfterId(scanner, startId));

        while(scanner.hasNext() && recipes.size() < amount){
            Recipe r = scanRecipe(scanner);
            if(r!=null)
                recipes.add(r);
        }

        } catch (FileNotFoundException e) {
            //File does not exist
        }
        return recipes;
    }

    /*
     * Method: readRecipes
     * Reads a given amount of recipes from file, starting from the first one.
     * Returns: The resulting list of recipes.
     */
    public static ArrayList<Recipe> readRecipes(int amount){

        ArrayList<Recipe> recipes = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(fileName))) {
            scanner.useDelimiter(";");

            //Skip the header
            for (int i = 0; i < 6; i++)
                scanner.next();

            while (scanner.hasNext() && recipes.size() < amount) {

                Recipe r = scanRecipe(scanner);
                if(r!=null)
                    recipes.add(r);
            }
        }
        catch (FileNotFoundException fnf) {
            //If file does not exist, an empty ArrayList is returned
        }
        return recipes;
    }

    /*
     * Method: readRecipes
     * Reads all recipes from file, starting from the first one.
     * Returns: The resulting list of recipes.
     */
    public static ArrayList<Recipe> readRecipes (){
        ArrayList<Recipe> recipes = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(fileName))) {
            scanner.useDelimiter(";");

            //Skip the header
            for (int i = 0; i < 6; i++)
                scanner.next();

            while (scanner.hasNext()) {
                Recipe r = scanRecipe(scanner);
                if(r!=null)
                    recipes.add(r);
            }
        }
        catch (FileNotFoundException fnf) {
            //If file does not exist, an empty ArrayList is returned
        }
        return recipes;
    }
    /*
     * Method: scanRecipe
     * Scans a single recipe. Parameter scanner must be prepared (skipped headers etc).
     * This method is only to be used as a tool for other file reading methods in order to reduce redundancy
     * and to make the code more difficult to follow.
     * Parameter scanner: Scanner to be used
     * Returns: scanned Recipe.
     */
    private static Recipe scanRecipe(Scanner scanner){

        long recipeId;

        //Reading id
        try {
            //ID might have a \n character from previous line
            if(scanner.hasNext()) {
                String idString = scanner.next().replaceAll("\n", "");
                if (!idString.equals(""))
                    recipeId = Integer.parseInt(idString);
                else return null; //EOF reached
            }
            else return null; //EOF reached
        }
        catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            //ID messed up, exit loop
            return null;
        }
        return scanRecipeAfterId(scanner,recipeId);
    }

    /*
     * Method: scanRecipeAfterID
     * Scans the recipe when the scanner has advanced past the ID.
     * Parameter scanner: Scanner to be used
     * Parameter recipeId: Id of recipe to be scanned
     * Returns scanned recipe
     */
    private static Recipe scanRecipeAfterId(Scanner scanner, long recipeId){

        String recipeName;
        String recipeInstructions;
        String recipeIngredients;
        int recipeDuration;
        ArrayList<Integer> recipeCategories = new ArrayList<>();

        //Reading recipe name
        if(scanner.hasNext())
            recipeName = removeQuotes(scanner.next());
        else return null;

        //Reading recipe instructions
         if(scanner.hasNext())
             recipeInstructions = removeQuotes(scanner.next());
         else return null;

         //Reading recipe ingredients
         if(scanner.hasNext())
             recipeIngredients = removeQuotes(scanner.next());
         else return null;

         //Reading duration
         try {
             recipeDuration = Integer.parseInt(scanner.next());
         }
         catch (NumberFormatException nfe) {
             //Something wrong with duration field, exit loop
             return null;
         }

         //Reading categories

         if(scanner.hasNext()) {
              String catString = scanner.next();
              //If recipe has no categories  (marked as -1), an empty ArrayList will be used.
               if(!catString.equals( "-1")){
                    catString = removeQuotes(catString);
                    String [] cats = catString.split(",");
                    for(String cat : cats)
                        try{
                           recipeCategories.add(Integer.parseInt(cat));
                        }
                        catch(NumberFormatException nfe){
                          //If something is wrong with categories in file, just return empty ArrayList
                          recipeCategories.clear();
                          break;
                         }
               }
         }
            else return null;

         return new Recipe(recipeId,recipeName,recipeInstructions, recipeIngredients,recipeDuration,recipeCategories);
    }


    /*
     * Method: advanceScannerToRecipe
     * Advances the scanner to just after the id of the recipe specified by recipeId
     * Parameter scanner: scanner to be used
     * Parameter recipeId: id of the recipe to which the scanner shall advance
     * Returns: true if successful, false if the file is messed up or does not contain the recipe.
     */
    private static boolean advanceScannerToRecipe(Scanner scanner, long recipeId) {

        scanner.useDelimiter(";");

        //Skip the header
        for (int i = 0; i < 6 && scanner.hasNext(); i++)
            scanner.next();

        while (scanner.hasNext()) {
            String IdString = scanner.next().replaceAll("\n", "");
            try {
                if (recipeId == Integer.parseInt(IdString)) {
                    //Break the loop once the correct recipe has been reached
                    return true;
                }
            } catch (NumberFormatException nfe) {
                //EOF reached (or file messed up)
                return false;
            }
            //Skip all but ID
            for (int i = 0; i < 5 && scanner.hasNext(); i++)
                scanner.next();
        }
        return false;
    }

    /*
     * Method: nextId
     * Returns the next never-used recipe ID
     */
    public static long nextId()  {

        int nextFreeId=0;

        try (Scanner scanner = new Scanner(new File(fileName))){
            scanner.useDelimiter(";");

            //Skip the header
            for (int i = 0; i < 6 && scanner.hasNext(); i++)
                scanner.next();

            while (scanner.hasNext()) {
                String IdString = scanner.next().replaceAll("\n", "");
                try {
                    nextFreeId = Integer.parseInt(IdString);
                } catch (NumberFormatException nfe) {
                    //EOF reached
                }
                //Skip all but ID
                for (int i = 0; i < 5 && scanner.hasNext(); i++)
                    scanner.next();
            }

        } catch (FileNotFoundException e) {
            //File does not exist yet, can start ID at 1
            return 1;
        }
        return ++nextFreeId;
    }


    /*
     * Method: removeQuotes
     * Removes the first and last quotation marks in a string.
     * Assumes String starts and ends with quotes.
     * Parameter input: String to be modified
     * Returns: input without quotes
     */

    public static String removeQuotes (String input){
        input =  input.replaceFirst("\"", "");
        input =  input.substring(0,  input.length() - 1);

        return input;
    }

    /*
     * Method: generateRecipeString
     * Generates a String representation of a Recipe, complete with line separators, that can be written into
     * a csv file.
     * Parameter r: Recipe from which to generate the String
     * Returns: the generated String
     */
    public static String generateRecipeString(Recipe r){

        //File gets messed up if it contains extra semicolons, let's replace them
        r.setName(r.getName().replaceAll(";",":"));
        r.setIngredients(r.getIngredients().replaceAll(";",":"));
        r.setInstructions(r.getInstructions().replaceAll(";",":"));

        String recipeString = nextId() + ";\"" + r.getName() + "\";\""+ r.getInstructions() + "\";\"" + r.getIngredients() + "\";" + r.getDuration() + ";";
        if (r.getCategories().isEmpty())
            recipeString += "-1";
        else {
            recipeString +="\"";
            for (Integer category : r.getCategories())
                recipeString +=category + ","; //Using , to separate multiple categories

            recipeString +="\"";
        }
        recipeString +=";\n";

        return recipeString;
    }


    /*
     * Method: recipeInArrayList
     * Checks if recipe with the same id exists in an ArrayList. Returns the index if it does, -1 if not.
     * Parameter list: ArrayList to be checked
     * Parameter recipe_par: recipe to be looked up
     */
    public static int recipeInArrayList(ArrayList<Recipe> list, Recipe recipe_par){
        for(Recipe r : list){
            if(r.getId() == recipe_par.getId())
                return list.indexOf(r);
        }
        return -1;
    }

    /*
     * Method: searchByName
     * Checks if the searchString matches any recipe names in a given ArrayList.
     * Parameter recipes: list to be searched
     * Parameter searchString: String to be used for searching
     * Returns: List of matching recipes.
     */
    public static ArrayList<Recipe> searchByName(ArrayList<Recipe> recipes, String searchString){

        ArrayList <Recipe> resultSet = new ArrayList<>();

        for(Recipe r : recipes)
           if(r.getName().toLowerCase().contains(searchString.toLowerCase()))
               resultSet.add(r);

        return resultSet;
    }

    public static String getFileName(){
        return fileName;
    }
    public static void setFileName(String fileName_par){
        fileName = fileName_par;
    }
}
