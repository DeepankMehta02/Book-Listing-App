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

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    public BookAdapter(Activity context, ArrayList<Book> Books) {
        super(context, 0, Books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * Check if the existing view is reused, otherwise inflate the view
         */
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Book currentBook = getItem(position);
        Log.i(LOG_TAG, "Item position: " + position);

        /**
         * Find the text view in list_item
         */
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        TextView priceTextView = (TextView) listItemView.findViewById(R.id.price);
        TextView languageView = (TextView) listItemView.findViewById(R.id.language);
        TextView currencyView = (TextView) listItemView.findViewById(R.id.currency);

        titleTextView.setText(currentBook.getTitle());
        authorTextView.setText(currentBook.getAuthor());
        priceTextView.setText(String.valueOf(formatPrice(currentBook.getPrice())));
        languageView.setText(currentBook.getLanguage());
        currencyView.setText(currentBook.getCurrency());

        Log.i(LOG_TAG, "List has been created");
        return listItemView;

    }

    /**
     * Price format method
     */
    private String formatPrice(double price) {
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        return priceFormat.format(price);
    }
}
