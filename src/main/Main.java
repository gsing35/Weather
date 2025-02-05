package main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] agrs) {
        try {
            Scanner Scan = new Scanner(System.in);
            String city;
            do {
                System.out.println("Enter the city name: ");
                city = Scan.nextLine();
                
                if(city.equalsIgnoreCase("exit")) {
                    break;
                }

                JSONObject cityLocationData = (JSONObject)getLocationData(city);
                double longitude = (double)cityLocationData.get("longitude");
                double latitude = (double)cityLocationData.get("latitude");

                displayWeatherData(latitude, longitude);

            } while (!city.equalsIgnoreCase("exit"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static JSONObject getLocationData(String city) {
        city = city.replaceAll(" ", "+");   //Replaces all whitespace with "+" like in the URL

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + city + "&count=1&language=en&format=json";  //Had to edit API link to include city

        try {
            //Fetch API response based on APi URL
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            //Check response status
            //200 means the response is successful
            if(apiConnection.getResponseCode() != 200) {
                System.out.println("Error in fetching data from API");
                return null;
            }

            //Read the response from the API
            String jsonResponse = readApiResponse(apiConnection);

            //Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(jsonResponse);

            //Retreive Location Data
            JSONArray locationData = (JSONArray) response.get("results");
            return (JSONObject)locationData.get(0);

            

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            //attempt to crerate connection to the API
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //Set request method to GET
            connection.setRequestMethod("GET");

            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        
    }

    public static String readApiResponse(HttpURLConnection connection) {
        try {
            //Read the response from the API
            Scanner scan = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            //Loop through each line in response and append to StringBuilder
            while(scan.hasNextLine()) {
                response.append(scan.nextLine());
            }
            //Free up resources
            scan.close();
            //Return JSON as String
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Issue reading response
        return null;
    }

    public static Double displayWeatherData(double latitude, double longitude) {
        try {
            //Fetch API response based on API link
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";
            HttpURLConnection apiConnection = fetchApiResponse(url);

            //Check response status
            if(apiConnection.getResponseCode() != 200) {
                System.out.println("Error in fetching data from API");
                return null;
            }
            //Read response from API 
        String jsonResponse = readApiResponse(apiConnection);


        //Parse the JSON response
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
        JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

        //Store data into corresponding data type
        String time = (String) currentWeatherJson.get("time");
        System.out.println("Time: " + time);

        double temperature = (double) currentWeatherJson.get("temperature_2m");
        System.out.println("Temperature: " + temperature);

        long humidity = (long) currentWeatherJson.get("relative_humidity_2m");  
        System.out.println("Humidity: " + humidity);

        double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
        System.out.println("Wind Speed: " + windSpeed);

        }
        catch (Exception e) {
            e.printStackTrace();
    }
    return null;
    }
}
