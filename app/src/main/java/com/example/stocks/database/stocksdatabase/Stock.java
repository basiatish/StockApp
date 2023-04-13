package com.example.stocks.database.stocksdatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Stock {

    @PrimaryKey(autoGenerate = true)
    public int id = 0;

    @ColumnInfo(name = "Short_name")
    public String shortName;

    @ColumnInfo(name = "Full_name")
    public String name;

    @ColumnInfo(name = "Last_price")
    public double price;

    @ColumnInfo(name = "Price_change")
    public double priceChange;

    @ColumnInfo(name = "Price_change_percent")
    public double priceChangePercent;

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public void setPriceChangePercent(double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }
}
