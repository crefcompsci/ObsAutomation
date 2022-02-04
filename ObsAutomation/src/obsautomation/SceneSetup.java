package obsautomation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class SceneSetup {

    public static void main(String[] args) {
        try {
//Build out scenes in CREFScenesBase based on the pattern SceneGroupName x maxX, maxY with one of the combo scenes present
//Copy the json output of this file to CREFScenesBuilt.json and import those scenes back into OBS
            File f = new File("src/obsautomation/CREFScenesBase.json");
//            System.out.println(f.getAbsolutePath());
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String eachLine = br.readLine();

            while (eachLine != null) {
                sb.append(eachLine);
                sb.append("\n");
                eachLine = br.readLine();
            }
//            System.out.println(sb.toString());

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(sb.toString()).getAsJsonObject();

            JsonArray sceneOrder = json.get("scene_order").getAsJsonArray();
            JsonArray sources = json.get("sources").getAsJsonArray();

//            System.out.println(sceneOrder);
//            System.out.println("\n\n");
//            System.out.println(sources);
            for (int i = sceneOrder.size() - 1; i >= 0; i--) {
                JsonObject scene = sceneOrder.get(i).getAsJsonObject();
                String sceneName = scene.get("name").getAsString();
//                System.out.println("Scene Name: " + sceneName);

                for (int j = 0; j < sources.size(); j++) {
                    JsonObject source = sources.get(j).getAsJsonObject();
//System.out.println("     "  + source.get("name"));
                    if (source.get("name").getAsString().equals(sceneName)) {
                        JsonObject settings = source.getAsJsonObject("settings");
                        JsonArray items = settings.getAsJsonArray("items");
                        for (int k = 0; k < items.size(); k++) {
                            JsonObject item = items.get(k).getAsJsonObject();
                            String subSourceName = item.get("name").getAsString();
                            if (subSourceName.equals("COMBO SCENE 1") || subSourceName.equals("COMBO SCENE 2")) {
                                JsonObject scaleJson = item.getAsJsonObject("scale");
                                JsonObject pos = item.getAsJsonObject("pos");
//                                System.out.println("   scaleJson:" + scaleJson);
//                                System.out.println("   pos  :" + pos);
                                if (sceneName.startsWith("FullA ") || sceneName.startsWith("FullB ")) {
                                    createAndAddNewFullScene(pos, scaleJson, sceneName, parser, source, sources, sceneOrder);
                                } else if (sceneName.startsWith("SlidesA") || sceneName.startsWith("SlidesB ")) {
                                    createAndAddNewSlideScene(pos, scaleJson, sceneName, parser, source, sources, sceneOrder);
                                }

                            }
                        }
                    }
                }
            }
            System.out.println("\n\n\n\n");
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAndAddNewFullScene(JsonObject pos, JsonObject scaleJson, String sceneName, JsonParser parser, JsonObject source, JsonArray sources, JsonArray sceneOrder) {
        int posX = pos.get("x").getAsInt();
        int posY = pos.get("y").getAsInt();
        double scale = scaleJson.get("x").getAsDouble();
        String suffix = sceneName.substring(sceneName.indexOf(" ") + 1);
//                                    System.out.println(suffix);
        int x = Integer.valueOf(suffix.substring(suffix.indexOf(" ") + 1, suffix.indexOf(",")));
        int y = Integer.valueOf(suffix.substring(suffix.indexOf(",") + 1));
//                                    System.out.println("     " + x + " , " + y);

        int rows = x;
        int cols = y;
        int xIncrement = (int) (posX / (rows - 1.0));
        int yIncrement = (int) (posY / (cols - 1.0));
//                                    System.out.println(xIncrement + "  " + yIncrement);

        for (int y1 = cols; y1 > 0; y1--) {
            for (int x1 = rows; x1 > 0; x1--) {
                if (x != x1 || y != y1) {
//                                                System.out.println("     " + x1 + "," + y1);
//                                                System.out.println("       " + ((x1 - 1) * xIncrement) + "  " + ((y1 - 1) * yIncrement));

                    JsonObject newObject = parser.parse(source.toString()).getAsJsonObject();
                    final String newSceneName = sceneName.substring(0, sceneName.lastIndexOf(" ")) + " " + x1 + "," + y1;

                    updateNewObject(newObject, newSceneName, (x1 - 1) * xIncrement, (y1 - 1) * yIncrement);
                    sources.add(newObject);
                    JsonObject newScene = new JsonObject();
                    newScene.addProperty("name", newSceneName);
                    sceneOrder.add(newScene);
                }
            }
        }
    }

    private static void updateNewObject(JsonObject newObject, String sceneName, int x, int y) {
//        System.out.println(sceneName);
        newObject.remove("name");
        newObject.addProperty("name", sceneName);
        JsonObject settings = newObject.getAsJsonObject("settings");
        JsonArray items = settings.getAsJsonArray("items");
        for (int k = 0; k < items.size(); k++) {
            JsonObject item = items.get(k).getAsJsonObject();
            String subSourceName = item.get("name").getAsString();
            if (subSourceName.equals("COMBO SCENE 1") || subSourceName.equals("COMBO SCENE 2")) {
                JsonObject pos = item.getAsJsonObject("pos");
                pos.remove("x");
                pos.remove("y");
                pos.addProperty("x", x);
                pos.addProperty("y", y);
                return;
            }
        }
    }

    private static void createAndAddNewSlideScene(JsonObject pos, JsonObject scaleJson, String sceneName, JsonParser parser, JsonObject source, JsonArray sources, JsonArray sceneOrder) {
//these zeros are roughly the top of the lower left box.        
        int zeroX = 1217;
        int zeroY = 557;

        int posX = pos.get("x").getAsInt() - zeroX;
        int posY = pos.get("y").getAsInt() - zeroY;
        String suffix = sceneName.substring(sceneName.indexOf(" ") + 1);
//        System.out.println(suffix + "     " + posX + ", " + posY);
        int x = Integer.valueOf(suffix.substring(suffix.indexOf(" ") + 1, suffix.indexOf(",")));
        int y = Integer.valueOf(suffix.substring(suffix.indexOf(",") + 1));
//        System.out.println("     x:" + x + " , y:" + y);

        int rows = x;
        int cols = y;
        int xIncrement = (int) (posX / (rows - 1.0));
        int yIncrement = (int) (posY / (cols - 1.0));
//        System.out.println("       increments:" + xIncrement + "  " + yIncrement);

        for (int y1 = cols; y1 > 0; y1--) {
            for (int x1 = rows; x1 > 0; x1--) {
                if (x != x1 || y != y1) {
//                    System.out.println("     x1,y1:" + x1 + "," + y1);
//                    System.out.println("       " + ((x1 - 1) * xIncrement) + "  " + ((y1 - 1) * yIncrement));

                    JsonObject newObject = parser.parse(source.toString()).getAsJsonObject();
                    final String newSceneName = sceneName.substring(0, sceneName.lastIndexOf(" ")) + " " + x1 + "," + y1;

                    updateNewObject(newObject, newSceneName, zeroX + ((x1 - 1) * xIncrement), zeroY + ((y1 - 1) * yIncrement));
                    sources.add(newObject);
                    JsonObject newScene = new JsonObject();
                    newScene.addProperty("name", newSceneName);
                    sceneOrder.add(newScene);
                }
            }
        }
    }
}
