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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookLoader.class.getName();

    /**
     * Variable
     */
    private String newUrl;

    /**
     * Constructs a new BookLoader
     */
    public BookLoader(Context context, String url) {
        super(context);
        newUrl = url;
        Log.i(LOG_TAG, "Loaded");
    }

    @Override
    public void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG, "Force Load");
    }

    @Override
    public List<Book> loadInBackground() {
        if (newUrl == null) {
            return null;
        }
        // Perform the network request, parse the response
        // and extract a list of books
        List<Book> books = Utils.fetchBookData(newUrl);
        Log.i(LOG_TAG, "Loaded in background");
        return books;
    }
}
