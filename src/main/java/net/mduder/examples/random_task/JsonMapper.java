package net.mduder.examples.random_task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Reference: https://gist.github.com/codebutler/2339666
 * This 'helper' is used instead of GSON conversion as the requirements are minimal.
 * Note the use of TreeMap as the conversion type - this fulfills two requirements.
 * 1. The Map must be Serializable for storage and retrieval in the Main Activity bundle.
 * 2. The Map's keys must iterate in alphabetical order without needing a sort mechanism.
 */
public class JsonMapper {
    public static TreeMap<String, Object> toMap(JSONObject jsonObject) throws JSONException {
        TreeMap<String, Object> map = new TreeMap<>();
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(jsonObject.get(key)));
        }
        return map;
    }

    public static ArrayList<Object> toList(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList<>();
        /* Unfortunately, the foreach syntax does not apply to JSONArray */
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object object) throws JSONException {
        if (object == JSONObject.NULL) {
            return null;
        } else if (object instanceof JSONObject) {
            return toMap((JSONObject) object);
        } else if (object instanceof JSONArray) {
            return toList((JSONArray) object);
        } else {
            return object;
        }
    }
}
