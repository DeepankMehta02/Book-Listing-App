/*
 * Copyright 2018 Deepank Mehta. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * You may not use this file; except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * Distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * Limitations under the License.
 */

package com.deepankmehta.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class Utils {

    // Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Private constructor so that no one creates an object of this class
     */
    public Utils() {
    }

    /**
     * Return a Book object by passing out information about the first book
     * from the input bookJSON string
     */
    private static List<Book> extractBookFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            Log.println(Log.INFO, LOG_TAG, bookJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books.
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");
            Log.println(Log.INFO, LOG_TAG, String.valueOf(booksArray));

            // For each book in the booksArray, create an {@link Book} object
            for (int i = 0; i < booksArray.length(); i++) {

                // Get a single book at position i within the list of items (books)
                JSONObject currentBook = booksArray.getJSONObject(i);
                Log.println(Log.INFO, LOG_TAG, String.valueOf(currentBook));

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that book. + [authors] list
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "author"
                String author;

                // Check if JSONArray exist
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    Log.println(Log.INFO, LOG_TAG, String.valueOf(authors));

                    // Check JSONArray Returns true if this object has no mapping for name or if it has a mapping whose value is NULL
                    if (!volumeInfo.isNull("authors")) {
                        // Get 1st element
                        author = (String) authors.get(0);
                    } else {
                        // assign info about missing info about author
                        author = "*** unknown author ***";
                    }
                } else {
                    // assign info about missing info about author
                    author = "*** missing info of authors ***";
                }


                // For a given book, extract the JSONObject associated with the
                // key called "saleInfo", which represents a list of region and object RetailPrice{amount, currency}
                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");


                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // Extract the value for the key called "language"
                String language = volumeInfo.getString("language");

                // Extract the value for the key called "amount"
                double amount = retailPrice.getDouble("amount");

                // Extract the value for the key called "currencyCode"
                String currency = retailPrice.getString("currencyCode");



                // Extract the value for the key called "buyLink"
                String buyLink = (String) saleInfo.get("buyLink");

                // Create a new {@link Book} object with the title, author, coverImageUrl, price, currency and language
                // and url from the JSON response.
                Book bookItem = new Book(title, author, amount, currency, language, buyLink);

                // Add the new {@link Book} to the list of booksList.
                books.add(bookItem);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        // Return the list of books (booksList)
        return books;
    }

    /**
     * Returns the URL object
     */
    private static URL createUrl(String Url) {
        URL url = null;
        try {
            url = new URL(Url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL");
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHTTPRequest (URL url) throws IOException {

        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 20000;
        final int CORRECT_RESPONSE_CODE = 200;

        String jsonResponse = "";

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request connection was successful then
            // read the inputStream and parse the response
            if (urlConnection.getResponseCode() == CORRECT_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error Response Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            } if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the inputStream into a String which contaiins
     * the JSON response from the server
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Query the Google Books API and return a list of book objects
     */
    static List<Book> fetchBookData(String requestUrl) {

        final int SLEEP_TIME = 2000;

        // This is required to show progress bar
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create a URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
            Log.i(LOG_TAG, "HTTP request: OK");
        } catch (IOException e) {
            Log.i(LOG_TAG, "Problem making the HTTP request");
        }

        // Extract fields from JSON response and create a list of books
        List<Book> listBooks = extractBookFromJson(jsonResponse);

        // Return the list of books
        return listBooks;
    }
}

