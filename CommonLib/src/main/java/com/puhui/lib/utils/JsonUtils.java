package com.puhui.lib.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * 描    述:  <描述>
 * Created by tangjian on 2016/7/13.
 */
@SuppressWarnings("unused")
public class JsonUtils {
    public static String getString(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getString(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getNumberString(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getString(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    public static boolean getBoolean(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getBoolean(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getInt(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getInt(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLong(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getLong(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getDouble(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getDouble(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.00;
    }

    public static float getFloat(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return (float) jsonObject.getDouble(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0.00f;
    }

    /***
     * 得到JsonObject对象
     */
    public static JSONObject getJsonObject(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /***
     * 得到JsonObject对象
     */
    public static JSONObject getJsonObject(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getJSONObject(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 得到JsonArray对象
     */
    public static JSONArray getJSONArray(String jsonStr) {
        try {
            return new JSONArray(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 得到JsonArray对象
     */
    public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
        try {
            if (jsonObject.has(name)) {
                return jsonObject.getJSONArray(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 得到JsonObject对象
     */
    public static JSONObject getJsonObject(JSONArray jsonArray, int index) {
        try {
            if (null != jsonArray && index < jsonArray.length()) {
                return jsonArray.getJSONObject(index);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getInstance(Class clazz, JSONObject jsonObject) {
        Object object = null;
        try {
            object = Class.forName(clazz.getClass().getName()).newInstance();
            Field[] fields = clazz.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.getName().equals("serialVersionUID")
                        && !field.getName().equals("$change")) {// && $change
                    field.set(object, JsonUtils.getString(jsonObject, field.getName()));
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
