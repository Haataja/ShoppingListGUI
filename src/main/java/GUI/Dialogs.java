package GUI;

import GUI.database.ConnectionHelper;
import GUI.dropbox.DropboxHelper;
import fi.tamk.tiko.read.Parser;
import fi.tamk.tiko.write.JSONArray;
import fi.tamk.tiko.write.JSONObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Dialog class sets up different dialog boxes used in Main-class.
 *
 * @author Hanna Haataja, hanna.haataja@cs.tamk.fi
 * @version 3.0, 12/15/2018
 * @since 1.0
 */
public class Dialogs {

    /**
     * Sets the information dialog to GUI.
     */
    static void setCopyrightDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/list.png")));
        dialog.setTitle("Shopping list app");
        dialog.setHeaderText(null);
        dialog.setContentText("Copyright Hanna Haataja 2018");
        dialog.showAndWait();
    }

    /**
     * Sets the save file chooser to the GUI.
     * <p>
     * Writes shopping list to selected file in JSON format.
     *
     * @param data  List that holds items from the shopping list.
     * @param stage Stage that shows the dialog.
     */
    static void setSaveToFileDialog(List<ShoppingList> data, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Shopping list");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            if (writeFile(data, file)) {
                setSuccessDialog(file);
            }
        }

    }

    /**
     * Sets the information dialog about successful saving to GUI.
     */
    private static void setSuccessDialog(File file) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/open.png")));
        dialog.setTitle("Save succeed!");
        dialog.setHeaderText(null);
        dialog.setContentText("Save done: " + file.getName());
        dialog.showAndWait();
    }

    /**
     * Sets the read file chooser to GUI.
     * <p>
     * Only .json-files can be chosen and reads the file and appends
     * the content of the file to the {@link List} of items.
     *
     * @param data  List that holds items from the shopping list.
     * @param stage Stage that shows the dialog.
     */
    static void setReadFromFileDialog(List<ShoppingList> data, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Shopping list");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                Parser parser = new Parser();
                JSONObject object = parser.parse(text);
                JSONArray array = (JSONArray) object.get("list");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject listItem = (JSONObject) array.get(i);
                    data.add(new ShoppingList(listItem.get("item").toString(), (int) listItem.get("quantity")));
                }
            } catch (IOException ex) {
                System.out.println("Error while reading from file, " + ex.getMessage());
            }
        }
    }

    /**
     * Saves the current list to Dropbox by always asking the token.
     *
     * @param application Application is needed to open browser window.
     * @param data        List of the items saved.
     */
    public static void setSaveToDropbox(Application application, List<ShoppingList> data) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Save to Dropbox");
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")));
        String url = DropboxHelper.init();

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType link = new ButtonType("Open Dropbox", ButtonBar.ButtonData.FINISH);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL, link);

        dialog.getDialogPane().addEventFilter(ActionEvent.ACTION, e -> {
            if (e.getTarget().toString().contains("Open Dropbox")) {
                e.consume();
                application.getHostServices().showDocument(url);
            }
        });


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField fileNameField = new TextField();
        fileNameField.setPromptText("example.json");
        Platform.runLater(fileNameField::requestFocus);
        TextField tokenField = new TextField();
        tokenField.setPromptText("Dropbox code");

        grid.add(new Label("File name:"), 0, 0);
        grid.add(fileNameField, 1, 0);
        grid.add(new Label("Dropbox code:"), 0, 1);
        grid.add(tokenField, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(ok);
        loginButton.setDisable(true);

        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(new HBox(grid, new ImageView(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")))));

        Platform.runLater(fileNameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ok) {
                if (!fileNameField.getText().contains(".json")) {
                    fileNameField.setText(fileNameField.getText() + ".json");
                }
                return new Pair<>(fileNameField.getText(), tokenField.getText().trim());
            }
            return null;
        });


        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            String filename = result.get().getKey();
            String token = result.get().getValue();

            File inputFile = new File(filename);
            if (writeFile(data, inputFile)) {
                if (DropboxHelper.uploadToDropbox(token, inputFile)) {
                    setSuccessDialog(inputFile);
                }
            }
        }

    }

    private static boolean writeFile(List<ShoppingList> data, File inputFile) {
        try {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            for (ShoppingList item : data) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("item", item.getName());
                jsonItem.put("quantity", item.getQuantity());
                array.add(jsonItem);
            }
            object.put("list", array);
            FileWriter fileWriter = new FileWriter(inputFile);
            fileWriter.write(object.toJsonString());
            fileWriter.close();
            return true;
        } catch (IOException ex) {
            System.out.println("Error while saving to file, " + ex.getMessage());
        }
        return false;
    }

    /**
     * Loads file from the Dropbox by the file name and token.
     *
     * @param application Application is needed to open browser window.
     * @param data        The list the content of the file is appended.
     */
    public static void setLoadFromDropbox(Application application, List<ShoppingList> data) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")));
        dialog.setTitle("Load Dropbox");
        String url = DropboxHelper.init();

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType link = new ButtonType("Open Dropbox", ButtonBar.ButtonData.FINISH);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL, link);

        dialog.getDialogPane().addEventFilter(ActionEvent.ACTION, e -> {
            if (e.getTarget().toString().contains("Open Dropbox")) {
                e.consume();
                application.getHostServices().showDocument(url);
            }
        });


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField fileNameField = new TextField();
        fileNameField.setPromptText("example.json");
        TextField tokenField = new TextField();
        tokenField.setPromptText("Dropbox code");

        grid.add(new Label("File name:"), 0, 0);
        grid.add(fileNameField, 1, 0);
        Platform.runLater(fileNameField::requestFocus);
        grid.add(new Label("Dropbox code:"), 0, 1);
        grid.add(tokenField, 1, 1);

        Node loginButton = dialog.getDialogPane().lookupButton(ok);
        loginButton.setDisable(true);

        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
        dialog.getDialogPane().setContent(new HBox(grid, new ImageView(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")))));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ok) {
                if (!fileNameField.getText().contains(".json")) {
                    fileNameField.setText(fileNameField.getText() + ".json");
                }
                return new Pair<>(fileNameField.getText(), tokenField.getText().trim());
            }
            return null;
        });


        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            String filename = result.get().getKey();
            String token = result.get().getValue();


            String text = DropboxHelper.loadFromDropbox(token, filename);
            Parser parser = new Parser();
            if (text != null) {
                JSONObject object = parser.parse(text);
                JSONArray array = (JSONArray) object.get("list");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject listItem = (JSONObject) array.get(i);
                    data.add(new ShoppingList(listItem.get("item").toString(), (int) listItem.get("quantity")));
                }
            }

        }

    }

    /**
     * Sets dialog that tells files downloaded from Dropbox may take a while.
     */
    public static void setTakeWhile() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")));
        dialog.setTitle("Downloading");
        dialog.setHeaderText(null);
        dialog.setContentText("Getting your file, this may take a while.");
        dialog.showAndWait();
    }

    /**
     * Sets all the dialogs that relate to saving to H2 database.
     *
     * @param data List of items in the shopping list.
     */
    public static void setSaveToH2(List<ShoppingList> data) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/open.png")));
        alert.setTitle("Save to H2 database");
        alert.setHeaderText("This will automatically save to your H2 database.");
        alert.setContentText("Database info:\nDatabase: \"~/ShoppingList\", \nTable: \"ShoppingList\"\n" +
                "User name: sa \nPassword: (empty)\nthis will be created if one does not exist.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ConnectionHelper helper = new ConnectionHelper();
            try {
                helper.connect();
                helper.writeToDatabase(data);

                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                Stage dialogStage1 = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialogStage1.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/open.png")));
                dialog.setTitle("Save succeed!");
                dialog.setHeaderText(null);
                dialog.setContentText("Saved to H2!\nDatabase: \"~/ShoppingList\", \nTable: \"ShoppingList\"" +
                        "\nUser name: sa \nPassword: (empty)");
                dialog.showAndWait();
            } catch (Exception e) {
                System.out.println("Error while handling H2 database!");
                //e.printStackTrace();
                setErrorH2("There was an error your shopping list is not saved.");
            } finally {
                helper.close();
            }
        }

    }

    /**
     * Sets all the dialogs that relate to loading from H2 database.
     *
     * @param data List of items in the shopping list.
     */
    public static void setReadFromH2(List<ShoppingList> data) {
        ConnectionHelper helper = new ConnectionHelper();
        try {
            helper.connect();
            helper.readFromDatabase(data);
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/open.png")));
            dialog.setTitle("Succeed!");
            dialog.setHeaderText(null);
            dialog.setContentText("Items loaded from H2 database.");
            dialog.showAndWait();
        } catch (Exception e) {
            System.out.println("Error while handling H2 database: " + e.getMessage());
            //e.printStackTrace();
            setErrorH2("Cannot read from H2 database.");
        } finally {
            helper.close();
        }
    }

    private static void setErrorH2(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/open.png")));
        alert.setTitle("Error While Handling H2 Database");
        alert.setHeaderText(message);
        alert.setContentText("Before trying again try to:\nDisconnect other connections to database ~/shoppingList" +
                "\nMake sure you get to database with credentials:\n\t\tUser name: sa, Password:(empty)");

        alert.showAndWait();
    }

    /**
     * Sets the dialog that tells something went wrong in the handling of the Dropbox.
     * @param message Message shown to the user in header.
     */
    public static void setErrorDropBox(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Dialogs.class.getClassLoader().getResourceAsStream("images/dropbox.png")));
        alert.setTitle("Error While Handling Dropbox");
        alert.setHeaderText(message);
        String instruction;
        if (message.contains("downloading")) {
            instruction = "Before trying again make sure:\n" +
                    "- You have right name of the file\n- You copied whole of the token from Dropbox site\n";
        } else {
            instruction = "Before trying again make sure:\n" +
                    "- Your file has unique name\n- You copied whole of the token from Dropbox site\n";
        }
        alert.setContentText(instruction);

        alert.showAndWait();
    }
}
