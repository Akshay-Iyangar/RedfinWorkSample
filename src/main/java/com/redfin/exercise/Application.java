package com.redfin.exercise;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * {@link Application} is the main entry of the program. It takes the current local time
 * and then converts it into time and day and passes it to the API to get the list of
 * resturants that are open currently.
 */
public class Application {

  private static final int PAGINATION = 10;

  public static void main(String[] args) {
    // read the app token from the command line argument.
    String appToken = "";
    if (args.length != 0) {
      appToken = args[0];
    }
    // check the current local date time and make appropraite conversions.
    final LocalDateTime dateTime = LocalDateTime.now();
    final String hour = String.valueOf(dateTime.getHour());
    final String minutes = String.valueOf(dateTime.getMinute());
    final String currentTime = String.format("%s:%s", hour, minutes);
    final String day = String.valueOf(dateTime.getDayOfWeek().getValue());
    Scanner s = new Scanner(System.in);

    //instantiate the restaurantClient.
    final RestaurantClient restaurantClient = RestaurantClient.of(currentTime,
        day,
        appToken);

    //get the total count of the restaurant open at this point.
    int counter = restaurantClient.getTotalOpenRestaurantsCount();
    List<String> listOfRestaurants;
    int offset=0;
    //paginate till all the restaurants are displayed
    while (counter > PAGINATION) {
      listOfRestaurants = restaurantClient.getListOfRestaurants(PAGINATION, offset);
      listOfRestaurants.forEach(System.out::println);
      System.out.println("---------------------------------------------");
      System.out.println("Press Enter key to continue...");
      s.nextLine();
      offset += PAGINATION;
      counter -= PAGINATION;
    }
    listOfRestaurants = restaurantClient.getListOfRestaurants(PAGINATION, offset);
    listOfRestaurants.forEach(System.out::println);
  }
}
