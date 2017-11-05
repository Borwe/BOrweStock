package com.stocks.brian.orwestock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import stocks.model.CoinsHolder;
import stocks.model.Price;

/**
 * Created by Brian on 10/20/2017.
 */

public class SearchActivity extends AppCompatActivity {

    AutoCompleteTextView input;
    Button search;
    TextView note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_coin);

        input=(AutoCompleteTextView) findViewById(R.id.searchText);
        search=(Button)findViewById(R.id.searchButton);
        note=(TextView)findViewById(R.id.searchWarning);

        //please check and fix this
        ArrayList<String> data_store=new ArrayList<>();
        for(Price p:CoinsHolder.getPrices()){
            if(p.getCurrencies().size()>0){
                data_store.add(p.getCrypto().getFullName());
            }
        }
        ArrayAdapter<String> data=new ArrayAdapter<String>(SearchActivity.this,
                android.R.layout.simple_dropdown_item_1line,
                data_store);

        input.setAdapter(data);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=input.getText().toString();
                Iterator<Price> priceIterator = CoinsHolder.getPrices().iterator();
                int position=-1;
                int count=0;
                while(priceIterator.hasNext()){
                    Price p=priceIterator.next();
                    if(p.getCrypto().getFullName().contains(s)){
                        position=count;
                    }
                    ++count;
                }
                MainActivity.chosen_Search=position;
                finish();
            }
        });
    }
}
