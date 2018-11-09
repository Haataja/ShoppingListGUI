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


public class Main extends Application {
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    private BorderPane group;
    private Scene scene;
    private int width = Math.round((float) primaryScreenBounds.getWidth() / 3);
    private int height = 640;
    private TableView<Item> table;
    private final ObservableList<Item> data =
            FXCollections.observableArrayList();

    public static void main(String[] args) {
        System.out.println("Author: Hanna Haataja");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
        MenuItem toDropBox = new MenuItem("to Dropbox");
        MenuItem toH2 = new MenuItem("to H2-database");
        save.getItems().addAll(toFile, toDropBox, toH2);
        Menu open = new Menu("Open");
        MenuItem fromFile = new MenuItem("from file");
        MenuItem fromDropBox = new MenuItem("from Dropbox");
        MenuItem fromH2 = new MenuItem("from H2-database");
        open.getItems().addAll(fromFile,fromDropBox,fromH2);
        file.getItems().addAll(save, open);

        Menu about = new Menu("About");
        MenuItem help = new MenuItem("Help");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem info = new MenuItem("Info");
        info.setOnAction(event -> setDialog());
        about.getItems().addAll(help,separator,info);
        menu.getMenus().addAll(file,about);

        return menu;
    }

    private void setDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Shopping list app");
        dialog.setHeaderText("Copyright Hanna Haataja 2018");
        dialog.setContentText(null);
        dialog.showAndWait();
    }

    private VBox setTable() {
        Label label = new Label("SHOPPING LIST");
        label.setFont(new Font("Arial", 15));
        table = new TableView<>();
        table.setPrefHeight(0.8 * height);
        table.setEditable(true);
        TableColumn first = new TableColumn("Quantity");
        TableColumn second = new TableColumn("Item");
        second.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        second.setCellFactory(TextFieldTableCell.forTableColumn());
        second.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Item, String>>) t -> {
            if (t.getNewValue().length() > 0) {
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue());
            } else {
                t.getTableView().getItems().remove(t.getTablePosition().getRow());
            }
        });
        first.setCellValueFactory(new PropertyValueFactory<Item, Integer>("quantity"));
        first.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        first.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Item, Integer>>) t ->{
                if(t.getNewValue() > 0){
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setQuantity(t.getNewValue());
                } else {
                    t.getTableView().getItems().remove(t.getTablePosition().getRow());
                }
        });
        table.getColumns().addAll(second, first);
        table.setItems(data);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 20, 0, 50));
        vbox.getChildren().addAll(label, table);
        return vbox;
    }

    private HBox setBottom() {
        final TextField addItem = new TextField();
        addItem.setPromptText("What to add to list?");
        final TextField addQuantity = new TextField();
        addQuantity.setPromptText("how many?");


        final Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            int quantity = 0;
            try {
                quantity = Integer.parseInt(addQuantity.getText());
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
            if (addItem.getText().length() > 0) {
                data.add(new Item(addItem.getText(), quantity));
            }
            addItem.clear();
            addQuantity.clear();

        });
        addButton.setDefaultButton(true);
        HBox hBox = new HBox(addItem, addQuantity, addButton);
        hBox.setPadding(new Insets(0, 20, 20, 50));
        hBox.setSpacing(3);
        return hBox;
    }
}
