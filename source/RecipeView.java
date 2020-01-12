
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Class: RecipeView
 * Panel that displays recipe information
 */
public class RecipeView extends JPanel{

    private final int RECIPE_NAME_CHAR_LIMIT = 30;

    private JButton backButton, helpButton,editRecipeButton, deleteRecipeButton;
    private JLabel recipeName, durationLabel,ingredientLabel, instructionLabel;
    private JTextArea ingredientList,instructions;
    private JScrollPane ingredientScrollPane, instructionScrollpane;
    private Recipe recipe;
    private GUI parentGUI;
    private String helpMessage;
    private boolean browseOnBackButton;


    /*
     * RecipeView constructor.
     * Initializes and places all elements to their normal positions.
     * Parameter parentGUI_par: The GUI in which this panel is used.
     */
    public RecipeView(GUI parentGUI_par){

        parentGUI = parentGUI_par;

        setLayout(new GridBagLayout());

        recipe = new Recipe();
        helpMessage = "Tässä näet reseptin tiedot. Voit muokata reseptiä painamalla \"Muokkaa reseptiä\" -painiketta. \nVoit myös poistaa reseptin painamalla \"Poista resepti\". \nPääset takaisin edelliseen näkymään \"Takaisin\" -painikkeen avulla.";

        Dimension buttonDimension = new Dimension(150,50);

        backButton = new JButton("Takaisin");
        backButton.setPreferredSize(buttonDimension);
        backButton.addActionListener(new ButtonListener());

        helpButton = new JButton("Apua");
        helpButton.setPreferredSize(buttonDimension);
        helpButton.addActionListener(new ButtonListener());

        editRecipeButton = new JButton("<html>Muokkaa<br>reseptiä</html>");
        editRecipeButton.addActionListener(new ButtonListener());
        editRecipeButton.setPreferredSize(buttonDimension);

        deleteRecipeButton = new JButton ("Poista resepti");
        deleteRecipeButton.addActionListener(new ButtonListener());
        deleteRecipeButton.setPreferredSize(buttonDimension);

        recipeName = new JLabel(recipe.getName());
        recipeName.setFont(new Font("Arial",Font.PLAIN,30));

        durationLabel = new JLabel("Valmistuksen Kesto: " + recipe.getDuration() + "min");
        durationLabel.setFont(new Font("Arial",Font.PLAIN,15));

        ingredientLabel= new JLabel("Ainekset:");
        ingredientLabel.setFont(new Font("Arial",Font.PLAIN,15));

        ingredientList = new JTextArea(recipe.getIngredients());
        ingredientList.setEditable(false);
        ingredientList.setLineWrap(true);
        ingredientList.setWrapStyleWord(true);

        ingredientScrollPane = new JScrollPane(ingredientList);
        ingredientScrollPane.setPreferredSize(new Dimension ((int) buttonDimension.getWidth()*2, 1));

        instructionLabel = new JLabel ("Ohje:");
        instructionLabel.setFont(new Font("Arial",Font.PLAIN,15));

        instructions = new JTextArea(recipe.getInstructions());
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);

        instructionScrollpane = new JScrollPane(instructions);
        instructionScrollpane.setPreferredSize(new Dimension ((int) buttonDimension.getWidth()*3, 1));


        //Adding icons to buttons (dependent on 'icons' folder)

        try{
            backButton.setIcon(new ImageIcon(getClass().getResource("/icons/Back24.gif")));
            backButton.setHorizontalAlignment(SwingConstants.LEFT);
            backButton.setIconTextGap(16);

            helpButton.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            helpButton.setIconTextGap(16);
            helpButton.setHorizontalAlignment(SwingConstants.LEFT);

            editRecipeButton.setIcon(new ImageIcon(getClass().getResource("/icons/Edit24.gif")));
            editRecipeButton.setHorizontalAlignment(SwingConstants.LEFT);
            editRecipeButton.setIconTextGap(16);

            deleteRecipeButton.setIcon(new ImageIcon(getClass().getResource("/icons/Delete24.gif")));

        }
        catch (NullPointerException npe){
            System.out.println("icons folder is missing, no icons will be set.");
        }

        buildPanel();
    }

    /*
     * Method: buildPanel
     * Places all elements to their normal positions using GridBagConstraints.
     */
    public void buildPanel(){

        GridBagConstraints gbc = new GridBagConstraints();

        //Adding back button
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,32,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(backButton,gbc);

        //Adding recipe name label
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(recipeName,gbc);

        //Adding help button

        gbc.gridx=4;
        gbc.gridy=0;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,0,32);
        gbc.anchor= GridBagConstraints.LINE_END;
        gbc.fill= GridBagConstraints.NONE;

        add(helpButton,gbc);

        //Adding duration label

        gbc.gridx=0;
        gbc.gridy=1;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,32,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(durationLabel,gbc);

        //Adding edit recipe button

        gbc.gridx=3;
        gbc.gridy=1;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_END;
        gbc.fill= GridBagConstraints.NONE;
        add(editRecipeButton,gbc);

        //Adding delete recipe button
        gbc.gridx=4;
        gbc.gridy=1;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,0,32);
        gbc.anchor= GridBagConstraints.LINE_END;
        gbc.fill= GridBagConstraints.NONE;

        add(deleteRecipeButton,gbc);


        //Adding ingredient list label
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,32,0,0);
        gbc.anchor= GridBagConstraints.LAST_LINE_START;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        add(ingredientLabel,gbc);


        //Adding instruction label
        gbc.gridx=2;
        gbc.gridy=2;
        gbc.weightx=1;
        gbc.weighty=0.5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LAST_LINE_START;
        gbc.fill= GridBagConstraints.HORIZONTAL;

        add(instructionLabel,gbc);

        //Adding ingredient list
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.weightx=1;
        gbc.weighty=4;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,32,32,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.VERTICAL;
        add(ingredientScrollPane,gbc);

        //Adding instructions
        gbc.gridx=2;
        gbc.gridy=3;
        gbc.weightx=1;
        gbc.weighty=4;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.insets=new Insets(0,0,32,32);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.BOTH;
        add(instructionScrollpane,gbc);
    }

    /*
     * Method: setRecipe
     * Sets the recipe to be displayed, and updates the components to display it's information
     * Parameter recipe_par: the recipe to be displayed
     */
    public void setRecipe(Recipe recipe_par){

        recipe = recipe_par;

        if(recipe_par.getName().length()>RECIPE_NAME_CHAR_LIMIT)
            recipeName.setText(recipe_par.getName().substring(0,RECIPE_NAME_CHAR_LIMIT-3) + "...");
        else
            recipeName.setText(recipe_par.getName());

        ingredientList.setText(recipe_par.getIngredients());
        instructions.setText(recipe_par.getInstructions());
        if(recipe_par.getDuration()>=0)
            durationLabel.setText("Valmistusaika: " + recipe_par.getDuration() + "min");
        else
            durationLabel.setText("Valmistusaika ei tiedossa");
    }

    /*
     * Method: showHelpDialog
     * Displays a dialog box showing the help message specified in the helpMessage variable
     */
    public void showHelpDialog(){
        JOptionPane.showMessageDialog(null, helpMessage, "Apua", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * Method: confirmAndDelete
     * Warns the user about the deletion, and deletes a recipe if the user confirms it.
     * Returns: true if recipe was deleted, false if not.
     */
    public boolean confirmAndDelete() {

        Object[] options = {"Kyllä, poista", "Ei, peruuta"};
        int choice = JOptionPane.showOptionDialog(null, "Oletko varma, että haluat poistaa reseptin?", "Vahvista poisto",
                                                   JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

        if (choice == 0) {
            CookBookController.deleteRecipe(recipe);
            parentGUI.getHomePanel().removeFromLastViewed(recipe);
            return true;
        }
        else return false;
    }

    /*
     * Method: setBrowseOnBackButton
     * Setter function for the browseOnBackButton variable, used for determining whether the program should
     * switch to the browse view when the back button is pressed.
     * Parameter browseOnBackButton: new value of browseOnBackButton
     */
    public void setBrowseOnBackButton(boolean browseOnBackButton) {
        this.browseOnBackButton = browseOnBackButton;
    }

    /*
     * Class: ButtonListener
     * Handles ActionEvents triggered by the buttons on the panel
     */
    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == editRecipeButton && recipe.getId()!=0){
                parentGUI.setEditRecipeView(recipe);
            }
            else if (e.getSource() == deleteRecipeButton && recipe.getId()!=0){
                if(confirmAndDelete())
                    parentGUI.setHomeView();
            }
            else if (e.getSource() == backButton) {
                if(browseOnBackButton)
                    parentGUI.setBrowseView();
                else
                    parentGUI.setHomeView();
            }
            else if (e.getSource() == helpButton){
                showHelpDialog();
            }

        }
    }
}
