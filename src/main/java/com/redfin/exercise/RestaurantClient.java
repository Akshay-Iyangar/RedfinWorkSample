package com.redfin.exercise;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RestaurantClient} is used to perform operations like
 * 1. Get the count of restaurants that are open at any given time
 * 2. To list the restaurants for particular offset and limit.
 */
public class RestaurantClient {

  public static final String HTTPS = "https";
  public static final String HOST = "data.sfgov.org";
  public static final String PATH = "/resource/jjew-r69b.json";
  public static final String COUNT_DISTINCT_APPLICANT = "count_distinct_applicant";
  public static final String APPLICANT = "applicant_1";
  public static final String LOCATION = "location";
  private final ObjectMapper MAPPER = new ObjectMapper();
  private final OkHttpClient client;
  private final URIBuilder base;
  private final String dayOrder;
  private final String currentTime;
  private final String appToken;

  public static RestaurantClient of(String currentTime, String dayOrder, String appToken) {
    return new RestaurantClient(currentTime, dayOrder, appToken);
  }

  public RestaurantClient(String currentTime, String dayOrder, String appToken) {
    this.dayOrder = dayOrder;
    this.currentTime  = currentTime;
    this.appToken = appToken;
    client = new OkHttpClient();
    base = new URIBuilder().setScheme(HTTPS)
        .setHost(HOST)
        .setPath(PATH);
  }

  /**
   * This method is used to get the total count of restaurants that are open at any given point in time.
   */
  public int getTotalOpenRestaurantsCount() {

    try {
      final URI uri = base
          .setParameter("$select", "count(distinct(applicant))")
          .setParameter("$where", String.format("dayorder == '%s' and start24 <= '%s' and end24 >= '%s'",
              dayOrder,
              currentTime,
              currentTime))
          .build();
      Request request = buildRequest(uri, appToken); //need to make this parameterized.
      final JsonNode jsonNode = getResponseBodyOnSuccess(request);
      return jsonNode.get(0).get(COUNT_DISTINCT_APPLICANT).asInt();
    } catch (URISyntaxException e) {
      throw new RestaurantClientException("The given uri syntax's is not valid please check the uri", e);
    }
  }

  /**
   * This method is used to get list of the restaurants (name and address) based on limit and offset.
   * @param limit - Limit of restaurants for each page.
   * @param offset - current offset of the page.
   */
  public List<String> getListOfRestaurants(int limit, int offset) {
    final ArrayList<String> list = new ArrayList<>();
    try {
      final URI uri = base
          .setParameter("$select", "distinct(applicant),location")
          .setParameter("$where", String.format("dayorder == '%s' and start24 <= '%s' and end24 >= '%s'",
              dayOrder,
              currentTime,
              currentTime))
          .setParameter("$order", "applicant")
          .setParameter("$limit", String.valueOf(limit))
          .setParameter("$offset", String.valueOf(offset))
          .build();
      Request request = buildRequest(uri, appToken); //need to make this parameterized.
      final JsonNode jsonNode = getResponseBodyOnSuccess(request);

      jsonNode.spliterator().forEachRemaining( k -> {
        final JsonNode name = k.get(APPLICANT);
        final JsonNode location = k.get(LOCATION);
        list.add(String.format("Name : %s , Address : %s", name.asText(), location.asText()));
      });
      return list;
    } catch (URISyntaxException e) {
      throw new RestaurantClientException("The given uri syntax's is not valid please check the uri", e);
    }
  }

  private JsonNode getResponseBodyOnSuccess(Request request) {
    try {
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        final ResponseBody body = response.body();
        return MAPPER.readTree(body.bytes());
      } else {
        throw new RestaurantClientException(String.format("The response of the call to fetch restaurants count " +
            "was %d %s", response.code(), response.message()));
      }
    } catch (IOException e) {
      throw new  RestaurantClientException(" I/O exception when trying to fetch restaurants count", e);
    }
  }

  private static Request buildRequest(URI uri, String appToken) {
    return new Request.Builder()
        .url(uri.toString())
        .get()
        .addHeader("content-type", "application/json")
        .addHeader("X-App-Token", appToken) //need to make this parameterized.
        .build();
  }
}
