import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/*
 * Class GUI:
 * Contains the program window, and handles changes between different views.
 */

public class GUI {

    private JFrame mainFrame;
    private HomeView homePanel;
    private RecipeView recipePanel;
    private EditRecipeView editPanel;
    private BrowseView browsePanel;


    /*
     * GUI constructor.
     * Default constructor, initializes the window and all panels. Sets the starting view to the home view.
     */
    public GUI() {
        mainFrame = new JFrame("Keittokirja");
        mainFrame.setSize(1024, 768);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        homePanel = new HomeView(this);
        recipePanel = new RecipeView(this);
        editPanel = new EditRecipeView(this);
        browsePanel = new BrowseView(this);
        setHomeView();
    }



    /*
     * Method: setHomeView
     * Displays the home view in the mainFrame window
     */
    public void setHomeView() {

        mainFrame.getContentPane().removeAll();
        mainFrame.add(homePanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
        recipePanel.setBrowseOnBackButton(false);
    }

    /*
     * Method: setNewRecipeView
     * Displays the recipe adding view in the mainFrame window
     */
    public void setNewRecipeView(){

        mainFrame.getContentPane().removeAll();
        editPanel.setAddingMode();
        mainFrame.add(editPanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /*
     * Method: setRecipeView
     * Displays the view that shows recipe information.
     * Parameter r: Recipe to be displayed
     */

    public void setRecipeView(Recipe r){

        mainFrame.getContentPane().removeAll();
        recipePanel.setRecipe(r);
        homePanel.addToLastViewed(r);
        mainFrame.add(recipePanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /*
     * Method: setEditRecipeView
     * Displays the recipe editing view.
     * Parameter r: Recipe to be edited
     */

    public void setEditRecipeView(Recipe r){

        mainFrame.getContentPane().removeAll();
        editPanel.setEditMode(r);
        mainFrame.add(editPanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /*
     * Method: setBrowseView
     * Displays the recipe browsing view.
     * If no parameter is specified, recipes are read from the recipe file.
     * Recipes are displayed on the latest page, or page 1 if the view hasn't been used yet
     */

    public void setBrowseView(){

        browsePanel.updateRecipeList(browsePanel.getCurrentPageNo());
        mainFrame.getContentPane().removeAll();
        mainFrame.add(browsePanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
        recipePanel.setBrowseOnBackButton(true);
    }

    /*
     * Method: setBrowseView
     * Displays the recipe browsing view.
     * Recipes are displayed on the latest page, or page 1 if the view hasn't been used yet
     * Parameter recipesToDisplay: The recipes to be listed in the table
     */

    public void setBrowseView(ArrayList<Recipe> recipesToDisplay) {
        browsePanel.updateRecipeList(browsePanel.getCurrentPageNo(),recipesToDisplay);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(browsePanel);
        mainFrame.setVisible(true);
        mainFrame.revalidate();
        mainFrame.repaint();
        recipePanel.setBrowseOnBackButton(true);
    }

    /*
     * Method: terminate
     * Closes the window.
     */

    public void terminate(){
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
    }

    /*
     * Method: getHomePanel
     * Getter function for homePanel
     * returns: homepanel
     */
    public HomeView getHomePanel() {
        return homePanel;
    }

}
