import java.awt.event.*;
import java.lang.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

/*
 * Class: HomeView
 * Home panel with a search bar, options to add new recipes or browse existing ones. Also includes a list of last viewed recipes
 */
public class HomeView extends JPanel{

    private JTextField searchBox;
    private JButton searchButton,newRecipeButton,browseButton,exitButton,helpButton;
    private JList lastViewedJList;
    private GUI parentGUI;
    private String helpMessage, searchDefaultText;
    private JScrollPane listScrollPane;
    private ArrayList<Recipe> lastViewedArrList;
    private final int maxLastViewedSize;

    private Dimension bigButtonDimension = new Dimension(150,50);
    private Dimension smallButtonDimension = new Dimension(75,25);

    /*
     * HomeView Constructor
     * Initializes and places all elements to their normal positions.
     * Parameter: parentGUI_par The GUI in which this panel is used.
     */
    public HomeView(GUI parentGUI_par) {


        lastViewedArrList = new ArrayList<>();
        maxLastViewedSize = 5;
        parentGUI=parentGUI_par;

        setLayout(new GridBagLayout());

        helpMessage = "Jos haluat lisätä uuden reseptin, valitse \"Lisää uusi resepti\".\n Voit etsiä reseptejä kirjoittamalla reseptin nimen tai sen  osan hakupalkkiin \n ja painamalla \"Haku\". Voit selata kaikkia reseptejä\n valitsemalla \"Selaa reseptejä\".";

        helpButton= new JButton ("Apua");
        helpButton.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        helpButton.setIconTextGap(16);
        helpButton.setHorizontalAlignment(SwingConstants.LEFT);
        helpButton.addActionListener(new ButtonListener());

        searchDefaultText="Kirjoita haettavan reseptin nimi...";

        searchBox = new JTextField(searchDefaultText);
        searchBox.addFocusListener(new SearchBarListener());
        searchBox.addKeyListener(new EnterKeyListener());

        searchButton = new JButton("Haku");
        searchButton.setIcon(new ImageIcon(getClass().getResource("/icons/Zoom16.gif")));
        //Custom border prevents text from being cut off
        searchButton.setBorder(BorderFactory.createLineBorder(Color.gray));
        searchButton.addActionListener(new ButtonListener());

        newRecipeButton = new JButton("Uusi Resepti");
        newRecipeButton.addActionListener(new ButtonListener());
        browseButton = new JButton("Selaa reseptejä");
        browseButton.addActionListener(new ButtonListener());
        exitButton = new JButton("Sulje ohjelma");
        exitButton.addActionListener(new ButtonListener());

        DefaultListModel lModel = new DefaultListModel();
        lastViewedJList = new JList(lModel);
        lastViewedJList.addListSelectionListener(new ListListener());
        lastViewedJList.setFont(new Font("Arial", Font.PLAIN, 14));
        lastViewedJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lastViewedJList.setSelectedIndex(-1);
        lastViewedJList.setVisibleRowCount(5);
        lastViewedJList.setFixedCellHeight(30);
        lastViewedJList.setPreferredSize(new Dimension((int)(bigButtonDimension.getWidth()+smallButtonDimension.getWidth()),1));
        listScrollPane = new JScrollPane(lastViewedJList);
        listScrollPane.setColumnHeaderView(new JLabel ("Viimeksi katsotut Reseptit:"));


        helpButton.setPreferredSize(bigButtonDimension);
        newRecipeButton.setPreferredSize(bigButtonDimension);
        browseButton.setPreferredSize(bigButtonDimension);
        exitButton.setPreferredSize(bigButtonDimension);
        searchButton.setPreferredSize(smallButtonDimension);
        searchBox.setPreferredSize(new Dimension((int) bigButtonDimension.getWidth()*3,(int) smallButtonDimension.getHeight()));

        buildPanel();
        displayLastViewed();

    }

    /*
     * Method: buildPanel
     * Places all elements to their normal positions using GridBagConstraints.
     */
    public void buildPanel(){


        GridBagConstraints gbc = new GridBagConstraints();

        //Setting up the Help button
        gbc.gridx=6;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,(int)smallButtonDimension.getWidth(),0,32);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;
        add(helpButton,gbc);

        //Setting up the search bar
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.gridwidth=6;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,128,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.HORIZONTAL;
        add(searchBox, gbc);

        //Setting the search button
        gbc.gridx=6;
        gbc.gridy=1;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,0,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;
        add(searchButton, gbc);

        //Setting the new recipe button
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,128,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(newRecipeButton, gbc);

        //Setting up last viewed recipes
        gbc.gridx=5;
        gbc.gridy=2;
        gbc.gridwidth=2;
        gbc.gridheight=3;
        gbc.insets=new Insets(64,0,64,0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill= GridBagConstraints.VERTICAL;
        add (listScrollPane,gbc);

        //Setting up the browse button
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,128,0,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;
        add(browseButton, gbc);

        //Setting up the exit button
        gbc.gridx=0;
        gbc.gridy=4;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(64,128,64,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(exitButton, gbc);
    }

    /*
     * Method: showHelpDialog
     * Displays a dialog box showing the help message specified in the helpMessage variable
     */
    public void showHelpDialog(){
        JOptionPane.showMessageDialog(null, helpMessage, "Apua", JOptionPane.INFORMATION_MESSAGE);
    }

    /*
     * Method: showQuitDialog
     * Prompts the user for confirmation, and if confirmed, closes the window.
     */
    public void showQuitDialog(){

        Object[] options = {"Kyllä", "Ei"};
        int choice = JOptionPane.showOptionDialog(null, "Oletko varma, että haluat sulkea ohjelman?", "Varmista lopetus",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

        if(choice==0)
            parentGUI.terminate();

    }

    /*
     * Method: addToLastViewed
     * Adds a recipe to the last viewed recipes-list. Existing recipes will be bumped to the top of the list.
     * Parameter r: recipe to be added to the list
     */
    public void addToLastViewed(Recipe r){

        //Don't add empty recipes to last viewed
        if(r.getId()==0)
            return;

        //If a recipe already exists in list, move it to the top
        int existingLocation = CookBookController.recipeInArrayList(lastViewedArrList,r);

        if(existingLocation!=-1){
            lastViewedArrList.remove(existingLocation);
        }

        else if(lastViewedArrList.size()>=maxLastViewedSize)
            lastViewedArrList.remove(maxLastViewedSize - 1);

        lastViewedArrList.add(0,r);
        displayLastViewed();
    }

    /*
     * Method: removeFromLastViewed
     * Removes a recipe from the last viewed recipes list (if it exists in it).
     * Parameter r: Recipe to be removed
     */
    public void removeFromLastViewed(Recipe r){

        if(lastViewedArrList.contains(r))
            lastViewedArrList.remove(r);
        displayLastViewed();
    }
    /*
     * Method: displayLastViewed
     * Displays the list of last viewed recipes based on the recipes in lastViewedArrList
     */
    public void displayLastViewed(){

        DefaultListModel lModel;

        if(lastViewedJList.getModel() instanceof DefaultListModel) {
            lModel = (DefaultListModel) lastViewedJList.getModel();
            lModel.clear();
        }
        else return;

        if(lastViewedArrList.isEmpty()) {
            lModel.add(0,"Ei reseptejä");
        }
        else
            for (int i = 0; i < lastViewedArrList.size(); i++)
                lModel.add(i, lastViewedArrList.get(i).getName());

    }

    /*
     * Class: ButtonListener
     * Handles ActionEvents triggered by the buttons on the panel
     */
    private class ButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource()==newRecipeButton)
                parentGUI.setNewRecipeView();

            else if(e.getSource()==browseButton)
                parentGUI.setBrowseView();

            else if(e.getSource()==exitButton)
                showQuitDialog();

            else if (e.getSource() == helpButton)
                showHelpDialog();

            else if (e.getSource()==searchButton) {
                parentGUI.setBrowseView(CookBookController.searchByName(CookBookController.readRecipes(),searchBox.getText()));

            }

        }
    }

    /*
     * Class: ListListener
     * Sets the recipe view when a recipe is selected in last viewed recipes
     */
    private class ListListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if(e.getSource()==lastViewedJList && lastViewedJList.getSelectedIndex()>=0 && (!lastViewedArrList.isEmpty())){
                Recipe r = lastViewedArrList.get(lastViewedJList.getSelectedIndex());
                if(r.getId()!=0){
                    parentGUI.setRecipeView(r);
                }
            }

        }
    }

    /*
     * Class: SearchBarListener
     * Clears the guide text in the search bar when the bar is selected.
     * When focus is lost, guide text is returned if the user hasn't entered any text.
     */
    private class SearchBarListener implements FocusListener{

        @Override
        public void focusGained(FocusEvent e) {
            if(e.getSource()== searchBox && searchBox.getText().equals(searchDefaultText))
                searchBox.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(e.getSource() == searchBox && searchBox.getText().equals(""))
                searchBox.setText(searchDefaultText);

        }
    }
    /*
     * Class: EnterKeyListener
     * Makes search bar work with the enter key
     */
    private class EnterKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode()==KeyEvent.VK_ENTER)
                searchButton.doClick();
        }
    }

}
