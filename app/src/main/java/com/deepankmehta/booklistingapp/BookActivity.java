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

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = BookActivity.class.getSimpleName();

    /**
     * Constants
     */
    private static final int BOOK_LOADER = 1;
    ListView bookListView;
    boolean isConnected;

    /**
     * Variables
     */
    private String mUrlGoogleBooks = "";
    private TextView mEmptyTextView;
    private View mProgressBar;
    private BookAdapter mAdapter;
    private SearchView mSearchViewField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        // Find a reference to the ListView in the layout
        bookListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this,  new ArrayList<Book>());

        // Ser the adapter on the ListView
        bookListView.setAdapter(mAdapter);

        // EmptyTextView
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyTextView);

        // Search Button
        Button mSearchButton = (Button) findViewById(R.id.search_button);

        // ProgressBar
        mProgressBar = findViewById(R.id.progressbar);

        // SearchView
        mSearchViewField = (SearchView) findViewById(R.id.search_view_field);
        mSearchViewField.onActionViewExpanded();
        mSearchViewField.setIconified(true);
        mSearchViewField.setQueryHint("Enter a Tite");

        // Initialization of connectivity manager for checking internet connection
        final ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * Check the connection with internet and save in a boolean
         * if is connected is true then load LoaderManager
         * if is connected is false load emptyTextView
         */
        checkConnection(connectivityManager);

        if (isConnected) {
            // Get a reference to the LoaderManager
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader
            loaderManager.initLoader(BOOK_LOADER, null, this);
        } else {
            // ProgressBar mapping
            mProgressBar.setVisibility(View.GONE);
            // Display emptyTextView
            mEmptyTextView.setText("No Internet Connection");
        }

        // Sends a request to Google Books API including the search value
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection(connectivityManager);
                if (isConnected) {
                    // Update the URL and restart the loader
                    updateQueryUrl(mSearchViewField.getQuery().toString());
                    restartLoader();
                } else {
                    mAdapter.clear();
                    // Set emptyTextView visible
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    // Display the message
                    mEmptyTextView.setText("No Internet Connection");
                }
            }
        });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current book
                Book currentBook = mAdapter.getItem(position);
                // Convert the String URL into an URI object
                assert currentBook != null;
                Uri buyBookUri = Uri.parse(currentBook.getUrl());
                // Create a new Intent
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);
                // Start the new intent
                startActivity(websiteIntent);
            }
        });
    }

    public void checkConnection(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            isConnected = true;
        } else {
            isConnected = false;
        }
    }


    private String updateQueryUrl(String searchValue) {
        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }
        // Build a new string builder for the url
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.googleapis.com/books/v1/volumes?q=").append(searchValue).append("&filter=paid-ebooks&maxResults=40");
        mUrlGoogleBooks = stringBuilder.toString();
        return mUrlGoogleBooks;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader at the beginning
        updateQueryUrl(mSearchViewField.getQuery().toString());
        return new BookLoader(this, mUrlGoogleBooks);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // ProgressBar mapping
        mProgressBar.setVisibility(GONE);

        // Set text on emptyTextView
        mEmptyTextView.setText("No Books Found");

        // Clear the adapter
        mAdapter.clear();

        // If there is a valid list of books then add them to adapter's data set
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    public void restartLoader() {
        mEmptyTextView.setVisibility(GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER, null, BookActivity.this);
    }
}
