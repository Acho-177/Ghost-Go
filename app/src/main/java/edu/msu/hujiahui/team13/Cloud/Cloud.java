package edu.msu.hujiahui.team13.Cloud;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import edu.msu.hujiahui.team13.Cloud.Models.LoadResult;
import edu.msu.hujiahui.team13.Cloud.Models.SaveResult;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@SuppressWarnings("deprecation")
public class Cloud {
    private static final String MAGIC = "NechAtHa6RuzeR8x";
    private static final String BASE_URL = "https://webdev.cse.msu.edu/~hujiahui/cse476/project2/";
    private static final String FCMBASE_URL = "https://facweb.cse.msu.edu/dennisp/cse476FCM/";
    public static final String CATALOG_PATH = "chess-cat.php";
    public static final String SAVE_PATH = "chess-save.php";
    public static final String CREATE_PATH = "chess-create.php";
    public static final String DELETE_PATH = "hatter-delete.php";
    public static final String LOAD_CHESS_PATH = "chess-load.php";
    //public static final String UPLOAD_CHESS_PATH= "chess-upload.php";
    public static final String UPLOAD_CHESS_PATH= "game-matching.php";

    public static final String FCM_PATH = "fcm.php";
    public static final String REGISTER_PATH = "register.php";

    public static final String LOAD_PATH = "username-load.php";
    private static final String UTF8 = "UTF-8";

//    private static final String MAGIC = "NechAtHa6RuzeR8x";
//    private static final String USER = "wuhaoyu1";
//    private static final String PASSWORD = "123456";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    /**
     * Save a User to the cloud.
     * This should be run in a thread.
     * @param name name to save under
     * @param password password to save under
     * @return true if successful
     */
    public boolean saveUserToCloud(String name, String password) {
        name = name.trim();
        if(name.length() == 0) {
            return false;
        }

        // Create an XML packet with the information about the current image
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);

            xml.startTag(null, "chess");
            xml.attribute(null, "username", name);
            xml.attribute(null, "password", password);
            xml.endTag(null,  "chess");

            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return false;
        }

        Project3Service service = retrofit.create(Project3Service.class);
        final String xmlStr = writer.toString();
        try {
            Response<SaveResult> response = service.saveUser(writer.toString()).execute();
            SaveResult result = null;
            if (response.isSuccessful()) {
                result = response.body();
                if (result.getStatus() != null && result.getStatus().equals("yes")) {
                    return true;
                }
                Log.e("SaveToCloud", "Failed to save, message = '" + result.getMessage() + "'");
                return false;
            }
            Log.e("SaveToCloud", "Failed to save, message = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("SaveToCloud", "Exception occurred while trying to save hat!", e);
            return false;
        } catch (RuntimeException e) {
            Log.e("SaveToCloud", "Runtime exception: " + e.getMessage());
            //Log.e("SaveToCloud", "XML Error: " + rtn.errorBody() + " " + rtn.errorBody().string());
            return false;
        }
    }

//    /**
//     * Save a room to the cloud.
//     * This should be run in a thread.
//     * @param room name to save under
//     * @param view
//     * @return true if successful
//     */
//    public boolean createRoomToCloud(String room, String player, BoardView view, BoardActivity activity) {
//        room = room.trim();
//        if(room.length() == 0) {
//            return false;
//        }
//
//        // Create an XML packet with the information about the current image
//        XmlSerializer xml = Xml.newSerializer();
//        StringWriter writer = new StringWriter();
//
//        try {
//            xml.setOutput(writer);
//
//            xml.startDocument("UTF-8", true);
//
//
//
//            xml.startTag(null, "chessCreate");
//
//
//            xml.attribute(null, "room", room);
//            xml.attribute(null, "player1", player);
//            String chessesStr = activity.saveGameState();
//            /*Add all kind chessBoard status as mediumText*/
//            xml.attribute(null, "status", chessesStr);
//
//
//
//            //xml.attribute(null, "password", password);
//            xml.endTag(null,  "chessCreate");
//
//            xml.endDocument();
//
//        } catch (IOException e) {
//            // This won't occur when writing to a string
//            return false;
//        }
//
//        ChessService service = retrofit.create(ChessService.class);
//        final String xmlStr = writer.toString();
//        try {
//            Response<SaveResult> response = service.createRoom(writer.toString()).execute();
//            SaveResult result = null;
//            if (response.isSuccessful()) {
//                result = response.body();
//                if (result.getStatus() != null && result.getStatus().equals("yes")) {
//                    return true;
//                }
//                Log.e("SaveToCloud", "Failed to save, message = '" + result.getMessage() + "'");
//                return false;
//            }
//            Log.e("SaveToCloud", "Failed to save, message = '" + result.getMessage() + "'");
//            return false;
//        } catch (IOException e) {
//            Log.e("SaveToCloud", "Exception occurred while trying to save hat!", e);
//            return false;
//        } catch (RuntimeException e) {
//            Log.e("SaveToCloud", "Runtime exception: " + e.getMessage());
//            //Log.e("SaveToCloud", "XML Error: " + rtn.errorBody() + " " + rtn.errorBody().string());
//            return false;
//        }
//    }


    /**
     * Get User name from the cloud.
     * This should be run in a thread.
     * @param name name to save under
     * @param pw password to save under
     * @return true if successful
     */
    public String getUserNameFromCloud(String name, String pw) {
        Project3Service service = retrofit.create(Project3Service.class);
        try {
            Response<LoadResult> response = service.loadUser(name, pw).execute();
            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("getUserNameFromCloud", "Failed to load user name, response code is = " + response.code());
                return null;
            }

            LoadResult result = response.body();
            if (result.getStatus().equals("yes")) {
                return result.getStatus();
            }

            Log.e("getUserNameFromCloud", "Failed to load user name, message is = '" + result.getMessage() + "'");
            return null;
        } catch (IOException | RuntimeException e) {
            Log.e("getUserNameFromCloud", "Exception occurred while loading user name!", e);
            return null;
        }
    }


//    /**
//     * An adapter so that list boxes can display a list of filenames from
//     * the cloud server.
//     */
//    public static class CatalogAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {return catalog.getItems().size();}
//
//        @Override
//        public Item getItem(int position) {return catalog.getItems().get(position);}
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup parent) {
//            if(view == null) {
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
//            }
//
//            TextView tv = (TextView)view.findViewById(R.id.textItem);
//            tv.setText(catalog.getItems().get(position).getName());
//
//            return view;
//        }

//        /**
//         * The items we display in the list box. Initially this is
//         * null until we get items from the server.
//         */
//        private Catalog catalog = new Catalog("", new ArrayList(), "");
//
//        /**
//         * Constructor
//         */
//        public CatalogAdapter(final View view) {
//            // Create a thread to load the catalog
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        catalog = getCatalog();
//
//                        if (catalog.getStatus().equals("no")) {
//                            String msg = "Loading catalog returned status 'no'! Message is = '" + catalog.getMessage() + "'";
//                            throw new Exception(msg);
//                        }
//                        if (catalog.getItems().isEmpty()) {
//                            String msg = "Catalog does not contain any hattings.";
//                            throw new Exception(msg);
//                        }
//
//                        view.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                // Tell the adapter the data set has been changed
//                                notifyDataSetChanged();
//                            }
//
//                        });
//
//                    } catch (Exception e) {
//                        // Error condition! Something went wrong
//                        Log.e("CatalogAdapter", "Something went wrong when loading the catalog", e);
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                String string;
//                                // make sure that there is a message in the catalog
//                                // if there isn't use the message from the exception
//                                if (catalog.getMessage() == null) {
//                                    string = e.getMessage();
//                                } else {
//                                    string = catalog.getMessage();
//                                }
//                                Toast.makeText(view.getContext(), string, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }).start();
//        }
//
//
//        // Create a GET query
//        public Catalog getCatalog() throws IOException, RuntimeException {
//            ChessService service = retrofit.create(ChessService.class);
//
//
//            Response response = service.getCatalog().execute();
//            // check if request failed
//            if (!response.isSuccessful()) {
//                Log.e("getCatalog", "Failed to get catalog, response code is = " + response.code());
//                return new Catalog("no", new ArrayList<Item>(), "Server error " + response.code());
//            }
//            Catalog catalog = (Catalog) response.body();
//            if (catalog.getStatus().equals("no")) {
//                String string = "Failed to get catalog, msg is = " + catalog.getMessage();
//                Log.e("getCatalog", string);
//                return new Catalog("no", new ArrayList<Item>(), string);
//            }
//            if (catalog.getItems() == null) {
//                catalog.setItems(new ArrayList<Item>());
//            }
//
//            return catalog;

//            //temp test code
//            Catalog newCatalog = new Catalog("", new ArrayList(), "");
//
//            Item a = new Item();
//            a.setName("first item");
//            a.setId("id");
//            newCatalog.getItems().add(a);
//
//            Item b = new Item();
//            b.setName("second item");
//            b.setId("id");
//            newCatalog.getItems().add(b);
//
//            return newCatalog;
//        }
//
//        public String getId(int position) {
//            return catalog.getItems().get(position).getId();
//        }
//
//        public String getName(int position) {
//            return catalog.getItems().get(position).getName();
//        }
//    }
}
