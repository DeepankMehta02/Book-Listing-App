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

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }

    };

    /**
     * Title of the book
     */
    private final String title;
    /**
     * Author of the book
     */
    private final String author;
    /**
     * Price of the book
     */
    private final Double price;
    /**
     * Price of the book
     */
    private final String currency;
    /**
     * Country code of language
     */
    private final String language;
    /**
     * Url of the book
     */
    private String urlBook;


    public Book(String bookTitle, String authorName, Double bookPrice, String currencyCode, String languageCode, String buyLink) {
        title = bookTitle;
        author = authorName;
        price = bookPrice;
        currency = currencyCode;
        language = languageCode;
        urlBook = buyLink;

    }

    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        price = (Double) in.readValue(Double.class.getClassLoader());
        currency = in.readString();
        urlBook = in.readString();
        language = in.readString();
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLanguage() {
        return language;
    }

    public String getUrl() {
        return urlBook;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeValue(price);
        dest.writeString(currency);
        dest.writeString(urlBook);
        dest.writeString(language);

    }
}
