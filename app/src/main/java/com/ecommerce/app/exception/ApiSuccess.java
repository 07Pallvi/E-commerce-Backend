package com.ecommerce.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.*;

@Data
public class ApiSuccess {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date timestamp;

	@Schema(description = "status")
	private HttpStatus status;

	@Schema(description = "data")
	private Map<String, Object> data;

	private ApiSuccess() {
		timestamp = new Date();
	}

	public ApiSuccess(HttpStatus status) {
		this();
		this.status = status;
	}

	public ApiSuccess(Object object, HttpStatus status) {
		this();
		this.status = status;
		this.data = convertToMap(object);
	}

	public static Map<String, Object> convertToMap(Object object) {
		if (object instanceof JSONObject) {
			try {
				return toMap((JSONObject) object);
			} catch (JSONException e) {
				e.printStackTrace();
				return new HashMap<>();
			}
		}
		return new HashMap<>();
	}

	public static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
		Map<String, Object> map = new HashMap<>();
		Iterator<String> keys = jsonObject.keys();

		while (keys.hasNext()) {
			String key = keys.next();
			Object value = jsonObject.get(key);

			// Handle nested JSON objects and arrays
			if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			} else if (value instanceof JSONArray) {
				value = jsonArrayToList((JSONArray) value);
			}

			map.put(key, value);
		}

		return map;
	}

	public static List<Object> jsonArrayToList(JSONArray jsonArray) throws JSONException {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			Object value = jsonArray.get(i);
			if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			} else if (value instanceof JSONArray) {
				value = jsonArrayToList((JSONArray) value);
			}
			list.add(value);
		}
		return list;
	}

}
