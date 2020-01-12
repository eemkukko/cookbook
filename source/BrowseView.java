import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

/*
 * Class: BrowseView
 * Panel that displays a list of recipes and different sorting and filtering options.
 */
public class BrowseView extends JPanel{

    private JButton backButton, helpButton, searchButton, resetSearchButton;
    private JLabel titleLabel, filterLabel, searchResultLabel, sortLabel;
    private JTextField searchBar;
    private JTable recipeTable;
    private DefaultTableModel tModel;
    private JScrollPane recipeListPane;
    private ArrayList<JCheckBox> filterBoxes;
    private JSeparator filterSeparator;
    private JComboBox sortDropdown;
    private JLabel currentPage;
    private JButton firstPage, prevPage, nextPage, lastPage;
    private String[] categoryNames;
    private GUI parentGUI;
    private String helpMessage,searchDefaultText;
    private boolean searchMode;

    private ArrayList<Recipe>recipes;
    private int maxPages;
    private int currentPageNo;
    private int recipesPerPage;

    /*
     * BrowseView Constructor
     * Initializes and places all elements to their normal positions.
     * Also reads all recipes on file and displays them in the table,
     * distributed to each page based on the recipePerPage value.
     * Parameter: parentGUI_par The GUI in which this panel is used.
     */
    public BrowseView (GUI parentGUI_par){

        currentPageNo=1;
        recipesPerPage=10;
        parentGUI=parentGUI_par;
        setLayout(new GridBagLayout());

        helpMessage = "Taulukossa näet keittokirjan reseptit. Voit Avata reseptin klikkaamalla sen nimeä. \n Voit etsiä reseptejä kirjoittamalla sen nimen tai sen osan hakupalkkiin ja painamalla \"Haku\" painiketta.\n Voit näyttää vain tiettyjen kategorioiden reseptit vasemman reunan valintojen avulla. \n Alareunan painikkeiden avulla voit siirtyä keittokirjan sivulta toiselle. ";
        searchDefaultText="Kirjoita haettavan reseptin nimi...";

        Dimension bigButtonDimension = new Dimension(150,50);
        Dimension smallButtonDimension = new Dimension(75,25);
        Font normalTextFont = new Font("Arial",Font.PLAIN,14);
        Font normalTextFontBold = new Font ("Arial", Font.BOLD,14);

        tModel = new DefaultTableModel(0,2);
        tModel.setColumnIdentifiers(new String [] {"Reseptin nimi", "Valmistusaika"});

        //Initializing table
        for(int i=0;i<recipesPerPage;i++){
            String nameStringy =( "Resepti" + (i+1));
            tModel.insertRow(i , new String[]{nameStringy,"45"});
        }

        recipeTable = new JTable(tModel);

        recipeTable.setCellSelectionEnabled(true);
        ListSelectionModel cellSelectionModel = recipeTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cellSelectionModel.addListSelectionListener(new RecipeSelectionListener());

        recipeTable.setDefaultEditor(Object.class, null); //Makes table not editable by user

        recipeTable.getColumnModel().getColumn(0).setMinWidth(300);
        recipeTable.getColumnModel().getColumn(1).setMinWidth(200);
        recipeTable.getColumnModel().getColumn(1).setMaxWidth(200);
        recipeTable.setRowHeight(42); //Row height of 42 seems to fit nicely with default window size

        recipeTable.getTableHeader().setFont(normalTextFontBold);
        recipeTable.setFont(normalTextFont);

        recipeListPane = new JScrollPane(recipeTable);



        backButton = new JButton("Takaisin");
        backButton.addActionListener(new ButtonListener());
        backButton.setPreferredSize(bigButtonDimension);

        helpButton = new JButton ("Apua");
        helpButton.addActionListener(new ButtonListener());
        helpButton.setPreferredSize(bigButtonDimension);

        searchButton = new JButton ("Haku");
        searchButton.addActionListener(new ButtonListener());
        searchButton.setPreferredSize(smallButtonDimension);

        titleLabel = new JLabel("Reseptit");
        titleLabel.setFont(new Font("Arial",Font.PLAIN,30));

        searchBar = new JTextField(searchDefaultText);
        searchBar.setPreferredSize(new Dimension((int) bigButtonDimension.getWidth()*3,(int) smallButtonDimension.getHeight()));
        searchBar.addFocusListener(new SearchBarListener());
        searchBar.addKeyListener(new EnterKeyListener());

        searchResultLabel = new JLabel("Hakutulokset: ");
        searchResultLabel.setFont(normalTextFont);

        resetSearchButton = new JButton("Nollaa haku");
        resetSearchButton.addActionListener(new ButtonListener());

        filterLabel = new JLabel ("Näytä:");
        sortLabel = new JLabel ("Järjestä: ");
        sortLabel.setFont(normalTextFont);

        filterSeparator = new JSeparator(JSeparator.VERTICAL);
        filterSeparator.setPreferredSize(new Dimension(5,1));

        String [] sortOptions = {"Uusin ensin","Vanhin ensin","Aakkosjärjestys","Käänt. Aakkosjärj.", "Valmistusaika, nouseva", "Valmistusaika, laskeva"};
        sortDropdown = new JComboBox(sortOptions);
        sortDropdown.setSelectedIndex(0);
        sortDropdown.setFont(normalTextFontBold);
        sortDropdown.addActionListener(new SortOptionListener());


        categoryNames = new String[] {"Alkupalat","Lämpimät ateriat","Jälkiruuat","Välipalat","Keitot","Salaatit"};
        filterBoxes = new ArrayList();

        JCheckBox allCatsBox = new JCheckBox("Kaikki");
        allCatsBox.setSelected(true);
        allCatsBox.addActionListener(new FilterOptionListener());
        filterBoxes.add(0,allCatsBox);

        for(int i=0; i<categoryNames.length; i++){
            JCheckBox catBox = new JCheckBox(categoryNames[i]);
            catBox.setSelected(true);
            catBox.addActionListener(new FilterOptionListener());
            filterBoxes.add(i+1,catBox);
        }


        firstPage = new JButton("Ensim.");
        firstPage.addActionListener(new ButtonListener());
        firstPage.setPreferredSize(smallButtonDimension);

        prevPage = new JButton ("Edel.");
        prevPage.addActionListener(new ButtonListener());
        prevPage.setPreferredSize(smallButtonDimension);

        currentPage = new JLabel ("Sivu: 69/420");
        nextPage = new JButton ("Seur.");
        nextPage.addActionListener(new ButtonListener());
        nextPage.setPreferredSize(smallButtonDimension);

        lastPage = new JButton ("Viim.");
        lastPage.addActionListener(new ButtonListener());
        lastPage.setPreferredSize(smallButtonDimension);
        updateRecipeList(1);
        buildPanel();

        try{
            backButton.setIcon(new ImageIcon(getClass().getResource("/icons/Back24.gif")));
            backButton.setHorizontalAlignment(SwingConstants.LEFT);
            backButton.setIconTextGap(16);

            helpButton.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
            helpButton.setIconTextGap(16);
            helpButton.setHorizontalAlignment(SwingConstants.LEFT);

            //Custom borders prevent text from being cut off
            searchButton.setBorder(BorderFactory.createLineBorder(Color.gray));
            searchButton.setIcon(new ImageIcon(getClass().getResource("/icons/Zoom16.gif")));

            firstPage.setBorder(BorderFactory.createLineBorder(Color.gray));
            firstPage.setIcon(new ImageIcon(getClass().getResource("/icons/Rewind16.gif")));

            prevPage.setBorder(BorderFactory.createLineBorder(Color.gray));
            prevPage.setIcon(new ImageIcon(getClass().getResource("/icons/Back16.gif")));

            nextPage.setBorder(BorderFactory.createLineBorder(Color.gray));
            nextPage.setIcon(new ImageIcon(getClass().getResource("/icons/Forward16.gif")));

            lastPage.setBorder(BorderFactory.createLineBorder(Color.gray));
            lastPage.setIcon(new ImageIcon(getClass().getResource("/icons/FastForward16.gif")));

        }
        catch (NullPointerException npe){
            System.out.println("icons folder is missing, no icons will be set.");
        }
        setSearchMode(false);
        updateSearchMode();
    }

    /*
     * Method: buildPanel
     * Places all elements to their normal positions using GridBagConstraints.
     */
    public void buildPanel(){
        GridBagConstraints gbc = new GridBagConstraints();


        //placing back button
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets=new Insets(32,32,32,0);
        gbc.anchor= GridBagConstraints.LINE_START;
        gbc.fill= GridBagConstraints.NONE;

        add(backButton,gbc);

        //placing title
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.insets = new Insets(32,0,32,0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        add(titleLabel, gbc);

        //placing help button
        gbc.gridx=6;
        gbc.gridy=0;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(32,0,32,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;

        add(helpButton,gbc);

        //placing search bar
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,64,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(searchBar,gbc);

        //placing search button
        gbc.gridy = 1;
        gbc.gridx = 5;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,64,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;

        add(searchButton,gbc);

        //placing filter label
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,32,0,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;

        add(filterLabel,gbc);

        //placing search result label
        gbc.gridx=2;
        gbc.gridy=2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;

        add(searchResultLabel,gbc);

        //placing search-resetting button
        gbc.gridx=3;
        gbc.gridy=2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;

        add(resetSearchButton,gbc);

        //placing sorting label
        gbc.gridx=4;
        gbc.gridy=2;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,1,0);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.NONE;

        add(sortLabel,gbc);

        //placing dropdown menu for sorting
        gbc.gridx=5;
        gbc.gridy=2;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,1,32);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(sortDropdown,gbc);

        //Placing separator between filters and recipe list

        gbc.gridx=1;
        gbc.gridy=3;
        gbc.gridwidth=1;
        gbc.gridheight = filterBoxes.size();
        gbc.insets = new Insets(0,16,0,16);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.VERTICAL;

        add(filterSeparator,gbc);

        //Adding recipe list
        gbc.gridx=2;
        gbc.gridy=3;
        gbc.weighty=1;
        gbc.weightx=1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = filterBoxes.size();
        gbc.insets = new Insets(0,0,32,32);
        gbc.anchor=GridBagConstraints.LINE_START;
        gbc.fill=GridBagConstraints.BOTH;

        add(recipeListPane,gbc);
        gbc.weighty=0;

        //Adding filter buttons
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,32,16,0);
        gbc.anchor=GridBagConstraints.NORTHWEST;
        gbc.fill=GridBagConstraints.NONE;

        for(JCheckBox fBox : filterBoxes){
            add(fBox,gbc);
            gbc.insets.bottom=0;
            gbc.gridy++;
        }

        //Adding navigation buttons

        //Adding first page button
        gbc.gridx=2;
        gbc.gridy=4+filterBoxes.size();
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,32);
        gbc.anchor=GridBagConstraints.LINE_END;
        gbc.fill=GridBagConstraints.NONE;

        add(firstPage,gbc);

        //Adding previous button
        gbc.gridx=3;
        gbc.gridy=4+filterBoxes.size();
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor=GridBagConstraints.LINE_START;
        gbc.fill=GridBagConstraints.NONE;

        add(prevPage,gbc);

        //Adding page number label
        gbc.gridx=4;
        gbc.gridy=4+filterBoxes.size();
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor=GridBagConstraints.LINE_START;
        gbc.fill=GridBagConstraints.NONE;

        add(currentPage,gbc);

        //Adding next page button
        gbc.gridx=5;
        gbc.gridy=4+filterBoxes.size();
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,32);
        gbc.anchor=GridBagConstraints.LINE_END;
        gbc.fill=GridBagConstraints.NONE;

        add(nextPage,gbc);

        //adding last page button
        gbc.gridx=6;
        gbc.gridy=4+filterBoxes.size();
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.insets = new Insets(0,0,0,0);
        gbc.anchor=GridBagConstraints.LINE_START;
        gbc.fill=GridBagConstraints.NONE;

        add(lastPage,gbc);
    }

    /*
     * Method: showHelpDialog
     * Displays a dialog box showing the help message specified in the helpMessage variable
     */
    public void showHelpDialog(){
        JOptionPane.showMessageDialog(null, helpMessage, "Apua", JOptionPane.INFORMATION_MESSAGE);
    }


    /*
     * Method: updateRecipeList
     * Updates the recipes displayed in the JTable recipeTable according to the given arraylist and ordered in currently
     * selected manner of sorting. If a list of recipes is not specified, all recipes are read from file and shown.
     * Parameter pageNo: the number of the page to be displayed
     */
    public void updateRecipeList(int pageNo){
        updateRecipeList(pageNo, CookBookController.readRecipes());
    }


    /*
     * Method: updateRecipeList
     * Updates the recipes displayed in the JTable recipeTable according to the given arraylist and ordered in currently
     * selected manner of sorting. If a list of recipes is not specified, all recipes are read from file and shown.
     * Parameter pageNo: the number of the page to be displayed
     * Parameter recipes_par: an Arraylist of recipes to be displayed
     */
    public void updateRecipeList(int pageNo, ArrayList<Recipe> recipes_par) {

        currentPageNo = pageNo;
        recipes = recipes_par;

        recipes = filterRecipes(recipes);

        switch (sortDropdown.getSelectedIndex()){

            //0: Newest first
            case 0:
                Collections.reverse(recipes);
                break;
            //1: Oldest first (done automatically by readrecipes())
            case 1:
                break;
            //2: Alphabetical, A-Z
            case 2:
                CookBookController.sortAZ(recipes,true);
                break;
            //3: Alphabetical, Z-A
            case 3:
                CookBookController.sortAZ(recipes,false);
                break;
            //4: Duration, ascending
            case 4:
                CookBookController.sortDuration(recipes,true);
                break;
            //5: Duration, descending
            case 5:
                CookBookController.sortDuration(recipes,false);
                break;
            //default: Undefined; do nothing
            default:
                break;

        }


        //Adding empty recipes to fill the table
        while(recipes.size()%recipesPerPage != 0 || recipes.size()==0){
            recipes.add(new Recipe());
        }

        maxPages = recipes.size()/recipesPerPage;

        //Prevent OutOfBoundsException when maxpages decreases due to recipe being deleted
        if(currentPageNo > maxPages)
            currentPageNo=1;

        int startIndex = (currentPageNo-1) * recipesPerPage;

        for(int i=0;i<recipesPerPage;i++){

            tModel.setValueAt(recipes.get(startIndex+i).getName(),i,0);

            if (recipes.get(startIndex+i).getDuration() >= 0 )
                tModel.setValueAt(recipes.get(startIndex+i).getDuration() + " min",i,1);
            else
                tModel.setValueAt("",i,1);
        }

        recipeTable.clearSelection();
        currentPage.setText("Sivu: " + currentPageNo + "/" + maxPages);
    }

    /*
     * Method: filterRecipes
     * Removes recipes from the list that don't belong to the categories currently selected
     * Parameter recipes_par: recipes to be filtered
     * Returns: the filtered list of recipes
     */
    public ArrayList<Recipe> filterRecipes(ArrayList<Recipe> recipes_par){

        //If all selected, no filtering required
        if(filterBoxes.get(0).isSelected())
            return recipes_par;

        ArrayList<Recipe> resultSet = new ArrayList<>();

        //Getting list of selected categories
        //Note: Recipe uses 0 index to represent the first category, while filterboxes starts categories at 1 due to all button occupying 0
        ArrayList <Integer> selectedFilters = new ArrayList<>();
        for(JCheckBox box : filterBoxes){
            if (box.isSelected())
                selectedFilters.add(filterBoxes.indexOf(box)-1);
        }

        //Adding matching recipes to result set
        for(Recipe r : recipes_par){
            for (Integer recipeCategory : r.getCategories()){
                if(selectedFilters.contains(recipeCategory)) {
                    resultSet.add(r);
                    break;
                }
            }
        }

        return resultSet;
    }

    /*
     * Method: getCurrentpageNo
     * Getter function for the currentPageNo variable
     * Returns: value of currentPageNo
     */
    public int getCurrentPageNo() {
        return this.currentPageNo;
    }
    /*
     * Method: setCurrentpageNo
     * Setter function for the currentPageNo variable
     * Parameter currentPageNo: value that currentPageNo shall be set to
     */
    public void setCurrentPageNo(int currentPageNo_par) {
        this.currentPageNo = currentPageNo_par;
    }

    /*
     * Method: setSearchMode
     * Setter function for the searchMde variable
     * Parameter mode_par: whether search mode should be turned on
     */
    public void setSearchMode(boolean mode_par){
        this.searchMode = mode_par;
    }

    /*
     * Method: getSearchMode
     * Getter function for the searchMode variable
     * Returns: value of searchMode
     */
    public boolean getSearchMode(){
        return searchMode;
    }

    /*
     * Method: updateSearchMode
     * Updates the panel based on whether search mode is turned on or off.
     * If searchmode is on, The number of results and a button to cancel search are displayed.
     * If searchmode is off, these elements will be hidden.
     */
    public void updateSearchMode(){

        searchResultLabel.setVisible(searchMode);
        resetSearchButton.setVisible(searchMode);

        if(searchMode){
            int realRecipeCount=0;
            for(Recipe r: recipes){
                if(r.getId()>0)
                    realRecipeCount++;
            }
            searchResultLabel.setText("Hakutuloksia: " + realRecipeCount + " kpl");
        }
        else {
            updateRecipeList(1);
            searchBar.setText(searchDefaultText);
        }
    }

    /*
     * Class: ButtonListener
     * Handles ActionEvents triggered by the buttons on the panel
     */
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource()==helpButton){
                showHelpDialog();
            }
            else if (e.getSource() == backButton){
                parentGUI.setHomeView();
            }
            else if (e.getSource() == nextPage && currentPageNo < maxPages){
                updateRecipeList(++currentPageNo);
            }
            else if (e.getSource() == prevPage && currentPageNo > 1){
                updateRecipeList(--currentPageNo);
            }
            else if (e.getSource() == lastPage){
                updateRecipeList(maxPages);
            }
            else if (e.getSource() == firstPage){
                updateRecipeList(1);
            }
            else if (e.getSource() == searchButton){
                setSearchMode(true);
                parentGUI.setBrowseView(CookBookController.searchByName(CookBookController.readRecipes(),searchBar.getText()));
                updateSearchMode();
            }
            else if(e.getSource() == resetSearchButton){
                setSearchMode(false);
                updateSearchMode();
            }

        }
    }

    /*
     * Class: RecipeSelectionListener
     * Sets the recipe view when a recipe in the list is clicked
     */
    private class RecipeSelectionListener implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) {

            int selectedRow = recipeTable.getSelectedRow();

            if(e.getValueIsAdjusting() && selectedRow > -1 )
                parentGUI.setRecipeView(recipes.get(recipeTable.getSelectedRow() + ((currentPageNo-1)*recipesPerPage)));
        }
    }

    /*
     * Class: FilterOptionListener
     * Handles ActionEvents triggered by filter checkboxes being clicked
     */
    private class FilterOptionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == filterBoxes.get(0) && filterBoxes.get(0).isSelected())
                    for(JCheckBox box : filterBoxes)
                        box.setSelected(true);
            else
                filterBoxes.get(0).setSelected(false);

            updateRecipeList(1);

        }
    }

    /*
     * Class: SortOptionListener
     * Handles ActionEvents triggered by a sorting option being selected
     */
    private class SortOptionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateRecipeList(1);
        }
    }

    /*
     * Class: SearchBarListener
     * Clears the guide text in the search bar when the bar is selected.
     * When focus is lost, guide text is returned if the user hasn't entered any text.
     */
    private class SearchBarListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            if(e.getSource()== searchBar && searchBar.getText().equals(searchDefaultText))
                searchBar.setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(e.getSource() == searchBar && searchBar.getText().equals(""))
                searchBar.setText(searchDefaultText);

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
