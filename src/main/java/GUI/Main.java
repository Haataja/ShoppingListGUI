package GUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.IntegerStringConverter;

/**
 * Main class of the project. Constructs the graphical user interface.
 * @author Hanna Haataja, hanna.haataja@cs.tamk.fi
 * @version 2.0, 12/04/2018
 * @since 1.0
 */
public class Main extends Application {
    private Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    private BorderPane group;
    private Scene scene;
    private int width = Math.round((float) primaryScreenBounds.getWidth() / 3);
    private int height = 640;
    private TableView<ShoppingList> table;
    private final ObservableList<ShoppingList> data =
            FXCollections.observableArrayList();
    private Stage stage;

    /**
     * Starts the program.
     * @param args Inline arguments.
     */
    public static void main(String[] args) {
        System.out.println("Author: Hanna Haataja");
        launch(args);
    }

    /**
     * Launches the graphical user interface.
     * @param primaryStage the primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("Shopping list app");
        group = new BorderPane();
        group.setTop(new HBox(setMenu()));
        group.setCenter(setTable());
        group.setBottom(setBottom());
        scene = new Scene(group, width, height);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setX((primaryScreenBounds.getWidth() - scene.getWidth()) / 2f);
        primaryStage.setY((primaryScreenBounds.getHeight() - scene.getHeight()) / 2f);
        primaryStage.show();
        table.getColumns().get(1).setPrefWidth(table.getWidth() / 3);
        table.getColumns().get(0).setPrefWidth(2 * table.getWidth() / 3);
    }

    private MenuBar setMenu() {
        MenuBar menu = new MenuBar();
        menu.setPrefWidth(primaryScreenBounds.getWidth() - 1);
        Menu file = new Menu("File");
        Menu save = new Menu("Save");
        MenuItem toFile = new MenuItem("to file");
        toFile.setOnAction(e -> Dialogs.setSaveToFileDialog(data, stage));
        MenuItem toDropBox = new MenuItem("to Dropbox");
        toDropBox.setOnAction(e -> Dialogs.setSaveToDropbox(this, data));
        MenuItem toH2 = new MenuItem("to H2-database");
        save.getItems().addAll(toFile, toDropBox, toH2);
        Menu open = new Menu("Open");
        MenuItem fromFile = new MenuItem("from file");
        fromFile.setOnAction(e -> Dialogs.setReadFromFileDialog(data, stage));
        MenuItem fromDropBox = new MenuItem("from Dropbox");
        fromDropBox.setOnAction(e -> Dialogs.setLoadFromDropbox(this, data));
        MenuItem fromH2 = new MenuItem("from H2-database");
        open.getItems().addAll(fromFile, fromDropBox, fromH2);
        file.getItems().addAll(save, open);

        Menu about = new Menu("About");
        MenuItem help = new MenuItem("Help");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem info = new MenuItem("Info");
        info.setOnAction(event -> Dialogs.setCopyrightDialog());
        about.getItems().addAll(help, separator, info);
        menu.getMenus().addAll(file, about);

        return menu;
    }

    @SuppressWarnings("unchecked")
    private VBox setTable() {
        Label label = new Label("SHOPPING LIST");
        label.setFont(new Font("Arial", 15));
        table = new TableView<>();
        table.setPrefHeight(0.8 * height);
        table.setEditable(true);
        TableColumn first = new TableColumn("Quantity");
        TableColumn second = new TableColumn("ShoppingList");
        second.setCellValueFactory(new PropertyValueFactory<ShoppingList, String>("name"));
        second.setCellFactory(TextFieldTableCell.forTableColumn());
        second.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<ShoppingList, String>>) t -> {
            if (t.getNewValue().length() > 0) {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue());
            } else {
                t.getTableView().getItems().remove(t.getTablePosition().getRow());
            }
        });
        first.setCellValueFactory(new PropertyValueFactory<ShoppingList, Integer>("quantity"));
        first.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        first.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<ShoppingList, Integer>>) t -> {
            try {
                if (t.getNewValue() > 0) {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setQuantity(t.getNewValue());
                } else {
                    t.getTableView().getItems().remove(t.getTablePosition().getRow());
                }
            }catch (NullPointerException e){
                System.out.println("New value null -> remove row: " + e.getMessage() );
                t.getTableView().getItems().remove(t.getTablePosition().getRow());
            } catch (Exception ex){
                System.out.println("Error while modifying the quantity, " + ex.getMessage());
            }
        });
        table.getColumns().addAll(second, first);
        table.setItems(data);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 20, 0, 50));
        vbox.getChildren().addAll(label, setAdding(),table);
        return vbox;
    }

    private HBox setAdding() {
        final TextField addItem = new TextField();
        addItem.setPromptText("What to add to list?");
        final TextField addQuantity = new TextField();
        addQuantity.setPromptText("how many?");


        final Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            if(addItem.getText().length() > 0){
                int quantity = 0;
                try {
                    String text = addQuantity.getText().trim();
                    quantity = Integer.parseInt(text);
                } catch (RuntimeException ex) {
                    System.out.println("Error while parsing quantity text box, " + ex.getMessage());
                }
                if (addItem.getText().length() > 0) {
                    data.add(new ShoppingList(addItem.getText(), quantity));
                }
                addItem.clear();
                addQuantity.clear();
            }

        });
        addButton.setDefaultButton(true);
        HBox hBox = new HBox(addItem, addQuantity, addButton);
        //hBox.setPadding(new Insets(0, 20, 20, 50));
        hBox.setSpacing(5);
        return hBox;
    }

    private HBox setBottom(){
        final Button clear = new Button("Clear list");
        clear.setPrefWidth(100);
        clear.setOnAction(e -> data.clear());
        HBox hBox = new HBox(clear);

        hBox.setPadding(new Insets(5, 20, 20, 350));
        return hBox;
    }
}
