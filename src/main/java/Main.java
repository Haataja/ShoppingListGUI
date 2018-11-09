import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




public class Main extends Application {
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    private BorderPane group;
    private Scene scene;
    private int width = 480;
    private int height = 640;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Shopping list app");
        group = new BorderPane();
        group.setTop(new HBox(setMenu()));
        group.setCenter(setTable());
        scene = new Scene(group, width, height);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setX((primaryScreenBounds.getWidth()- scene.getWidth()) / 2f);
        primaryStage.setY((primaryScreenBounds.getHeight() - scene.getHeight()) / 2f);
        primaryStage.show();
    }
    private MenuBar setMenu() {
        MenuBar menu = new MenuBar();
        menu.setPrefWidth(primaryScreenBounds.getWidth());
        Menu file = new Menu("File");
        menu.getMenus().addAll(file);

        return menu;
    }


    private VBox setTable(){
        Label label = new Label("SHOPPING LIST");
        label.setFont(new Font("Arial", 15));
        TableView table = new TableView();
        table.setMaxWidth(primaryScreenBounds.getWidth()/3.0);
        table.setPrefHeight(0.8*height);
        table.setEditable(true);
        TableColumn first = new TableColumn("Quantity");
        TableColumn second = new TableColumn("item");
        table.getColumns().addAll(first,second);
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 20, 0, 50));
        vbox.getChildren().addAll(label, table);
        return vbox;
    }
}
