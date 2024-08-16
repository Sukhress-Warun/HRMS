package customUtils;

import org.json.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.stream.Collectors;

public class JsonUtils {

    public static HttpServletResponse prepareResponse(HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        return response;
    }

    public static JSONArray convertResultSetToJSONArray(ResultSet resultSet)
            throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_columns = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_columns; i++) {
                Object value = resultSet.getObject(i + 1);
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), (value != null) ? value : JSONObject.NULL);
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    public static JSONObject convertJSONArrayToJSONObject(String key, JSONArray jsonArray){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key,(jsonArray != null) ? jsonArray : JSONObject.NULL);
        return jsonObject;
    }


    public static JSONObject getRequestJSONObject(HttpServletRequest request) throws Exception{

        String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        return new JSONObject(test);
    }

    public static JSONObject formatJSONObject(String statusKey, Object statusValue, String message, String key, Object value){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(statusKey, (statusValue != null) ? statusValue : JSONObject.NULL);
        jsonObject.put("message", (message != null) ? message : JSONObject.NULL);
        if(key != null){
            jsonObject.put(key, (value != null) ? value : JSONObject.NULL);
        }
        return jsonObject;
    }

}
