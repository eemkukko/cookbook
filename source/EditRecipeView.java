import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/*
 * Class: EditRecipeView
 * Panel that allows the user to edit existing recipes or add new ones,
 * based on whether the addingMode variable is true.
 */
public class EditRecipeView extends JPanel{

    private JButton backButton,helpButton,saveButton,cancelButton;
    private JLabel titleLabel, nameFieldLabel, durationLabel, categoryLabel, ingredientLabel, instructionLabel;
    private JTextField nameField, durationField;
    private String[] categoryNames;
    private ArrayList<JCheckBox> categoryBoxes;
    private JTextArea ingredientArea,instructionArea;
    private JScrollPane ingredientScrollPane, instructionScrollPane;
    private JSeparator middleSeparator;
    private GUI parentGUI;
    private boolean addingMode;
    private Recipe recipe;
    private String helpMessage;

    /*
     * EditRecipeView constructor.
     * Initializes and places all elements to their normal positions.
     * Adding mode is enabled by default.
     * Parameter parentGUI_par: The GUI in which this panel is used.
     */

    public EditRecipeView(GUI parentGUI_par){

        parentGUI=parentGUI_par;
        setLayout(new GridBagLayout());

        Dimension buttonDimension = new Dimension(150,50);
        Dimension textFieldDimension = new Dimension (150, 20);

        helpMessage = "Kirjoita kenttiin reseptin tiedot. Huomaa, että nimi-kenttä on pakollinen. \nTallenna resepti tallenna-painikkeella.";

        titleLabel = new JLabel ();
        titleLabel.setFont(new Font("Arial",Font.PLAIN,20));

        backButton = new JButton("Takaisin");
        backButton.addActionListener(new ButtonListener());
        backButton.setPreferredSize(buttonDimension);

        helpButton = new JButton("Apua");
        helpButton.addActionListener(new ButtonListener());
        helpButton.setPreferredSize(buttonDimension);

        nameFieldLabel = new JLabel("Nimi (pakollinen): ");
        nameField = new JTextField();
        nameField.setPreferredSize(textFieldDimension);

        durationLabel = new JLabel ("Valmistusaika (min): ");
        durationField = new JTextField();
        durationField.setPreferredSize(textFieldDimension);

        categoryLabel = new JLabel("Kategoria(t):");

        categoryNames = new String[] {"Alkupalat","Lämpimät ateriat","Jälkiruuat","Välipalat","Keitot","Salaatit"};
        categoryBoxes = new ArrayList<>();

        //Creates category boxes based on the categorynames array
        for(int i=0; i<categoryNames.length; i++){
            categoryBoxes.add(i,new JCheckBox(categoryNames[i]));
        }


        ingredientLabel = new JLabel ("Ainekset: ");
        ingredientArea = new JTextArea();
        ingredientArea.setAutoscrolls(true);
        ingredientScrollPane = new JScrollPane(ingredientArea);
        ingredientScrollPane.setPreferredSize(new Dimension((int) buttonDimension.getWidth()*2,1));

        middleSeparator = new JSeparator(JSeparator.VERTICAL);
        middleSeparator.setPreferredSize(new Dimension(5,1));

        instructionLabel = new JLabel ("Reseptin ohjeet:");
        instructionArea = new JTextArea();
        instructionArea.setAutoscrolls(true);
        instructionScrollPane = new JScrollPane(instructionArea);
        instructionScrollPane.setPreferredSize(new Dimension((int) buttonDimension.getWidth()*2,1));

        saveButton   = new JButton("Tallenna");
        saveButton.addActionListener(new ButtonListener());
        saveButton.setPreferredSize(buttonDimension);

        cancelButton = new JButton ("Peruuta");
        cancelButton.addActionListener(new ButtonListener());
        cancelButton.setPreferredSize(buttonDimension);

        //Adding icons to buttons (dependent on 'icons' folder)

        try{
            backButton.setIcon(new ImageIcon(getClass().getResource("/icons/Back24.gif")));
            backButton.setHorizontalAlignment(SwingConstants.LEFT);
            backButton.setIconTextGap(16);

            helpButton.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            helpButton.setIconTextGap(16);
            helpButton.setHorizontalAlignment(SwingConstants.LEFT);


        }
        catch (NullPointerException npe){
            System.out.println("icons folder is missing, no icons will be set.");
        }

        setAddingMode();
        buildPanel();
    }

    /*
     * Method: buildPanel
     * Places all elements to their normal positions using GridBagConstraints.
     */
    public void buildPanel(){

        GridBagConstraints gbc = new GridBagConstraints();

        //View consists of a title row (top) and 2 panels on either side of the screen, separated by
        //a line
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());


        //Adding backButton
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0.5;
        gbc.weighty=1;
        gbc.insets=new Insets(32,32,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(backButton,gbc);

        //Adding the title
        gbc.gridx=1;
        gbc.gridy=0;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.weightx=0.5;
        gbc.weighty=1;
        gbc.insets=new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.CENTER;
        gbc.fill= GridBagConstraints.NONE;

        add(titleLabel,gbc);

        //Adding help button
        gbc.gridx=4;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0.5;
        gbc.weighty=1;
        gbc.insets=new Insets(32,0,0,32);
        gbc.anchor= GridBagConstraints.LINE_END;
        gbc.fill= GridBagConstraints.NONE;

        add(helpButton,gbc);



        //LEFT PANEL CONSTRUCTION
        //Adding name field & label
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(nameFieldLabel,gbc);

        gbc.gridx=1;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(nameField,gbc);

        //Adding duration field & label
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(durationLabel,gbc);

        gbc.gridx=1;
        gbc.gridy=1;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=0;
        gbc.weighty=0;
        gbc.insets=new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(durationField,gbc);



        //Adding category label
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets=new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(categoryLabel,gbc);

        /* Adding all category selection checkboxes in form:
        * 1, 2
        * 3, 4
        * 5, 6
        * etc.
        */
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        for(JCheckBox cat : categoryBoxes){
            if (categoryBoxes.indexOf(cat)%2 == 0) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
            else
                gbc.gridx++;
            leftPanel.add(cat,gbc);
        }


        //adding ingredient label
        gbc.gridx=0;
        gbc.gridy++;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets=new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        leftPanel.add(ingredientLabel,gbc);


        //Adding ingredient field
        gbc.gridx=0;
        gbc.gridy++;
        gbc.gridheight=1;
        gbc.gridwidth=2;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.BOTH;

        leftPanel.add(ingredientScrollPane,gbc);

        //Adding leftPanel to the window
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridheight=1;
        gbc.gridwidth=2;
        gbc.weightx=1;
        gbc.weighty=8;
        gbc.insets=new Insets(32,32,32,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.BOTH;

        add(leftPanel,gbc);

        //Adding a line between the left and right side of the window
        gbc.gridx=2;
        gbc.gridy=1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.gridwidth=1;
        gbc.weightx=0.5;
        gbc.weighty=1;
        gbc.insets = new Insets (32,0,32,0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.VERTICAL;

        add(middleSeparator,gbc);


        //CONSTRUCTING RIGHT PANEL
        //Adding label for the instruction field
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        rightPanel.add(instructionLabel,gbc);

        //Adding instruction field
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.weightx=1;
        gbc.weighty=1;
        gbc.insets=new Insets(0,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;

        rightPanel.add(instructionScrollPane,gbc);

        //Adding save button
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth  = 1;
        gbc.gridheight = 1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets = new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;

        rightPanel.add(saveButton,gbc);

        //Adding cancel button
        gbc.gridx=1;
        gbc.gridy=2;
        gbc.gridwidth = 1;
        gbc.gridheight= 1;
        gbc.weightx=1;
        gbc.weighty=0;
        gbc.insets = new Insets(32,0,0,0);
        gbc.anchor= GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.NONE;

        rightPanel.add(cancelButton,gbc);


        //Adding right panel
        gbc.gridx=3;
        gbc.gridy=1;
        gbc.gridwidth = 2;
        gbc.gridheight= 1;
        gbc.weightx=1;
        gbc.weighty=8;
        gbc.insets = new Insets(32,0,32,32);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        add(rightPanel,gbc);
    }

    /*
     * Method: setEditMode
     * Turns adding mode off and fills the input fields with information about the recipe to be edited.
     * Parameter r: Recipe to be edited.
     */
    public void setEditMode(Recipe r){

        addingMode=false;
        recipe = r;
        titleLabel.setText("Muokkaa reseptiä");
        nameField.setText(r.getName());
        if(r.getDuration()>0)
            durationField.setText("" + r.getDuration());
        else durationField.setText("");
        ingredientArea.setText(r.getIngredients());
        instructionArea.setText(r.getInstructions());

            for(JCheckBox cat : categoryBoxes){
                cat.setSelected(false);
            }

        if(!r.getCategories().isEmpty())
            for(Integer catIndex : r.getCategories()){
                categoryBoxes.get(catIndex).setSelected(true);
            }
    }
    /*
     * Method: setEditMode
     * Turns adding mode on and clears all input fields.
     */
    public void setAddingMode(){
        addingMode = true;
        titleLabel.setText("Lisää uusi resepti");
        nameField.setText("");
        durationField.setText("");
        ingredientArea.setText("");
        instructionArea.setText("");
        for(JCheckBox cat : categoryBoxes){
            cat.setSelected(false);
        }
    }

    /*
     * Method: showHelpDialog
     * Displays a dialog box showing the help message specified in the helpMessage variable
     */
    public void showHelpDialog(){
        JOptionPane.showMessageDialog(null, helpMessage, "Apua", JOptionPane.INFORMATION_MESSAGE);
    }
    /*
     * Method: showNameErrorDialog
     * Informs the user that a recipe must have a name.
     */
    public void showNameErrorDialog(){
        JOptionPane.showMessageDialog(null, "Reseptillä täytyy olla nimi.", "Nimi puuttuu", JOptionPane.INFORMATION_MESSAGE);
    }
    /*
     * Method: showDurationErrorDialog
     * Informs the user that the duration field can only contain numbers
     */
    public void showDurationErrorDialog(){
        JOptionPane.showMessageDialog(null, "Valmistusaika-kenttä voi sisältää vain numeroita.","Virhe", JOptionPane.INFORMATION_MESSAGE);
    }
    /*
     * Method: confirmExit
     * Prompts the user for confirmation for exiting the view without saving
     * Returns: true if user confirmed to exit, false if not
     */
    public boolean confirmExit(){

        Object[] options = {"Kyllä", "Ei"};
        int choice = JOptionPane.showOptionDialog(null, "Poistutaanko? Tehtyjä muutoksia ei tallenneta.", "Varmista lopetus",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

        if(choice==0)
            return true;
        else return false;
    }

    /*
     * Class: ButtonListener
     * Handles ActionEvents triggered by the buttons on the panel
     */
    private class ButtonListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {


            if (e.getSource() == saveButton) {

                try {
                    Recipe r = assembleRecipe();
                    if(addingMode)
                        CookBookController.newRecipeToFile(r);
                    else
                        CookBookController.updateRecipeFile(r);

                    parentGUI.setRecipeView(r);
                }
                catch (IllegalArgumentException iae){
                    if(iae instanceof NumberFormatException )
                        showDurationErrorDialog();
                    else
                        showNameErrorDialog();
                }
            }

            else if ((e.getSource() == cancelButton || e.getSource() == backButton) && confirmExit()){
                if(addingMode)
                    parentGUI.setHomeView();
                else
                    parentGUI.setRecipeView(recipe);
            }

            else if (e.getSource() == helpButton){
                showHelpDialog();
            }


        }

        public Recipe assembleRecipe() throws IllegalArgumentException {

            if (nameField.getText().equals(""))
                throw new IllegalArgumentException("Name field is empty");

            ArrayList<Integer> categories = new ArrayList<>();
            int i = 0;
            for(JCheckBox box : categoryBoxes) {
                if(box.isSelected())
                    categories.add(i);
                i++;
            }

            int duration=-1;

            if(!durationField.getText().equals(""))
                duration = Integer.parseInt(durationField.getText());

            long id;
            if(addingMode)
                id= CookBookController.nextId();
            else
                id = recipe.getId();

            return new Recipe (id, nameField.getText(),instructionArea.getText(), ingredientArea.getText(),  duration, categories);
        }
    }

}
