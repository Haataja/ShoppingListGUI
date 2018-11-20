package GUI;

import fi.tamk.tiko.read.Parser;
import fi.tamk.tiko.write.JSONArray;
import fi.tamk.tiko.write.JSONObject;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Dialog class sets up different dialog boxes used in Main-class.
 * @author Hanna Haataja, hanna.haataja@cs.tamk.fi
 * @version 1.0, 11/20/2018
 * @since 1.0
 */
public class Dialogs {

    /**
     * Sets the information dialog to GUI.
     */
    static void setCopyrightDialog() {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Shopping list app");
        dialog.setHeaderText(null);
        dialog.setContentText("Copyright Hanna Haataja 2018");
        dialog.showAndWait();
    }

    /**
     * Sets the save file chooser to the GUI.
     *
     * Writes shopping list to selected file in JSON format.
     *
     * @param data List that holds items from the shopping list.
     * @param stage Stage that shows the dialog.
     */
    static void setSaveToFileDialog(List<Item> data, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Shopping list");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                for (Item item : data) {
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put("item", item.getName());
                    jsonItem.put("quantity", item.getQuantity());
                    array.add(jsonItem);
                }
                object.put("list", array);
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(object.toJsonString());
                fileWriter.close();
            } catch (IOException ex) {
                System.out.println("Error while saving to file, " + ex.getMessage());
            }
        }
    }

    /**
     * Sets the read file chooser to GUI.
     *
     * Only .json-files can be chosen and reads the file and appends
     * the content of the file to the {@link List} of items.
     *
     * @param data List that holds items from the shopping list.
     * @param stage Stage that shows the dialog.
     */
    static void setReadFromFileDialog(List<Item> data, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Shopping list");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File file = fileChooser.showOpenDialog(stage);
        try {
            String text = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            Parser parser = new Parser();
            JSONObject object = parser.parse(text);
            JSONArray array = (JSONArray) object.get("list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject listItem = (JSONObject) array.get(i);
                data.add(new Item(listItem.get("item").toString(), (int) listItem.get("quantity")));
            }
        } catch (IOException ex) {
            System.out.println("Error while reading from file, " + ex.getMessage());
        }
    }
}
