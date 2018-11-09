import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private int width = Math.round((float) primaryScreenBounds.getWidth()/3);
    private int height = 640;
    private TableView<Item> table;
    private final ObservableList<Item> data =
            FXCollections.observableArrayList();

    public static void main(String[] args) {
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
        primaryStage.setX((primaryScreenBounds.getWidth()- scene.getWidth()) / 2f);
        primaryStage.setY((primaryScreenBounds.getHeight() - scene.getHeight()) / 2f);
        primaryStage.show();
        table.getColumns().get(1).setPrefWidth(table.getWidth()/3);
        table.getColumns().get(0).setPrefWidth(2*table.getWidth()/3);
    }
    private MenuBar setMenu() {
        MenuBar menu = new MenuBar();
        menu.setPrefWidth(primaryScreenBounds.getWidth() - 1);
        Menu file = new Menu("File");
        menu.getMenus().addAll(file);

        return menu;
    }


    private VBox setTable(){
        Label label = new Label("SHOPPING LIST");
        label.setFont(new Font("Arial", 15));
        table = new TableView<>();
        table.setPrefHeight(0.8*height);
        table.setEditable(true);
        TableColumn first = new TableColumn("Quantity");
        TableColumn second = new TableColumn("Item");
        second.setCellValueFactory(new PropertyValueFactory<Item,String>("name"));
        second.setCellFactory(TextFieldTableCell.forTableColumn());
        second.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Item, String>>) t ->
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue()));
        first.setCellValueFactory(new PropertyValueFactory<Item,Integer>("quantity"));
        first.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        first.setOnEditCommit((EventHandler<TableColumn.CellEditEvent<Item, Integer>>) t ->
                t.getTableView().getItems().get(t.getTablePosition().getRow()).setQuantity(t.getNewValue()));
        table.getColumns().addAll(second,first);
        table.setItems(data);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 20, 0, 50));
        vbox.getChildren().addAll(label, table);
        return vbox;
    }

    private HBox setBottom(){
        final TextField addItem = new TextField();
        addItem.setPromptText("What to add to list?");
        final TextField addQuantity = new TextField();
        addQuantity.setPromptText("how many?");


        final Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            int quantity = 0;
            try {
                 quantity = Integer.parseInt(addQuantity.getText());
            } catch (RuntimeException ex){
                System.out.println(ex.getMessage());
            }
            if(addItem.getText().length() > 0){
                data.add(new Item(addItem.getText(),quantity));
            }
            addItem.clear();
            addQuantity.clear();

        });
        HBox hBox = new HBox(addItem,addQuantity,addButton);
        hBox.setPadding(new Insets(0, 20, 20, 50));
        hBox.setSpacing(3);
        return hBox;
    }
}
