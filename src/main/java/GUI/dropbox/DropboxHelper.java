package GUI.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.v2.DbxClientV2;
import fi.tamk.tiko.read.Parser;
import fi.tamk.tiko.write.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * This class handles all things related to the Dropbox connection.
 *
 * @author Hanna Haataja, hanna.haataja@cs.tamk.fi
 * @version 2.0, 12/04/2018
 * @since 2.0
 */
public class DropboxHelper {
    private static DbxWebAuth auth;
    private static DbxRequestConfig config = new DbxRequestConfig("fi.tamk.tiko.haataja.ShoppingList/0.1");

    /**
     * Generates the url needed to authorize the app.
     *
     * @return String Authorization url.
     */
    public static String init() {
        DbxAppInfo appInfo = getAppInfo();
        if (appInfo != null) {
            auth = new DbxWebAuth(config, appInfo);
            DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                    .withNoRedirect()
                    .build();
            return auth.authorize(authRequest);
        }
        System.out.println("Appinfo null!");
        return null;
    }

    /**
     * Gets the client according to authToken given by client.
     *
     * @param authToken AuthToken fetched from the authentication page.
     * @return Client.
     */
    private static DbxClientV2 getClient(String authToken) {
        try {
            DbxAuthFinish authFinish = auth.finishFromCode(authToken);
            System.out.println(authFinish.getAccessToken());
            return new DbxClientV2(config, authFinish.getAccessToken());
        } catch (Exception e) {
            System.out.println("Error while finishing Dropbox: " + e.getMessage());
        }
        return null;
    }

    /**
     * Uploads the file to the Dropbox root.
     *
     * @param authToken authToken for getting the client.
     * @param inputFile File that is uploaded.
     */
    public static void uploadToDropbox(String authToken, File inputFile) {
        try (InputStream input = new FileInputStream(inputFile)) {
            getClient(authToken).files().uploadBuilder("/" + inputFile.getName()).uploadAndFinish(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the content of the file from the Dropbox.
     *
     * @param authToken authToken for getting the client.
     * @param name      The name of the file.
     * @return String content of the file.
     */
    public static String loadFromDropbox(String authToken, String name) {

        try {
            InputStream in = getClient(authToken).files().download("/" + name).getInputStream();
            String result = new BufferedReader(new InputStreamReader(in))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static DbxAppInfo getAppInfo() {
        try {
            Parser parser = new Parser();
            JSONObject object = parser.parse(new String(Files.readAllBytes(Paths.get("src/main/resources/test.app"))));
            return new DbxAppInfo((String) object.get("key"), (String) object.get("secret"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
