package GUI.dropbox;

import GUI.Dialogs;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import fi.tamk.tiko.read.Parser;
import fi.tamk.tiko.write.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DropboxHelper {
    private static DbxWebAuth auth;
    private static DbxRequestConfig config = new DbxRequestConfig("fi.tamk.tiko.haataja.ShoppingList/0.1");

    public static String init() {
        DbxAppInfo appInfo = getAppInfo();
        if(appInfo != null){
            auth = new DbxWebAuth(config, appInfo);
            DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
                    .withNoRedirect()
                    .build();
            return auth.authorize(authRequest);
        }
        System.out.println("Appinfo null!");
        return null;
    }

    private static DbxClientV2 getClient(String authToken){
        try{
            DbxAuthFinish authFinish = auth.finishFromCode(authToken);
            System.out.println(authFinish.getAccessToken());
            return new DbxClientV2(config, authFinish.getAccessToken());
        } catch (Exception e){
            System.out.println("Error while finishing Dropbox: " + e.getMessage());
        }
        return null;
    }

    public static void uploadToDropbox(String authToken, File inputFile){
        try (InputStream input = new FileInputStream(inputFile)) {
            getClient(authToken).files().uploadBuilder("/"+inputFile.getName()).uploadAndFinish(input);
        } catch (Exception e){
            e.printStackTrace();
        }
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
