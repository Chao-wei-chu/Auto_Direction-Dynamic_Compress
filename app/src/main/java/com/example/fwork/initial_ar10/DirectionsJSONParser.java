package com.example.fwork.initial_ar10;


		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;

		import org.json.JSONArray;
		import org.json.JSONException;
		import org.json.JSONObject;

		import android.util.Log;

		import com.google.android.gms.maps.model.LatLng;

/**
 * 接收一個JSONObject並返回一個列表的列表，包含經緯度
 * GPS導航解析用
 */

public class DirectionsJSONParser {

	public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for (int i = 0; i < jRoutes.length(); i++) {
				jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, String>>();
				List html = new ArrayList<HashMap<String, String>>();
				List dis = new ArrayList<HashMap<String, String>>();
				List gps = new ArrayList<HashMap<String, String>>();
				//List time = new ArrayList<HashMap<String, String>>();
				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++) {
					jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for (int k = 0; k < jSteps.length(); k++) {
						String polyline = "";
						polyline = (String) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for (int l = 0; l < list.size(); l++) {
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat",
									Double.toString(((LatLng) list.get(l)).latitude));
							hm.put("lng",
									Double.toString(((LatLng) list.get(l)).longitude));
							path.add(hm);
						}

						/** 取出html */
						HashMap<String, String> hp = new HashMap<String, String>();
						String html_str = "";
						html_str = (String) ((JSONObject) jSteps.get(k)).get("html_instructions");
						hp.put("html",html_str);
						html.add(hp);
						/** 每段path到達距離*/
						HashMap<String, String> hd = new HashMap<String, String>();
						String dis_str = "";
						dis_str = (String) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("distance")).get("text");
						hd.put("dis",dis_str);
						Log.i("Saving hd",hd+"");
						dis.add(hd);
						/** 每段path啟始GPS*/
						HashMap<String, String> hg = new HashMap<String, String>();
						Double gps_lat;
						Double gps_lng;
						gps_lat = (Double) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("start_location")).get("lat");
						gps_lng = (Double) ((JSONObject) ((JSONObject) jSteps
								.get(k)).get("start_location")).get("lng");
						hg.put("gps_lat",Double.toString(gps_lat));
						hg.put("gps_lng",Double.toString(gps_lng));
						gps.add(hg);
						Log.i("Saving hg",hg+"");

					}
					routes.add(path);
					routes.add(html);
					routes.add(dis);
					routes.add(gps);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}

		return routes;
	}

	/**
	 * 解碼折線點的方法
	 * */
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}