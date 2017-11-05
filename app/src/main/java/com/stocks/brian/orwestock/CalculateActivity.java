package com.stocks.brian.orwestock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import stocks.model.Price;

/**
 * Created by Brian on 10/20/2017.
 */

public class CalculateActivity extends AppCompatActivity {

    Price price;
    String currency;

    EditText crypto;
    EditText curr;
    Button convert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate);

        price= MainActivity.price_chosen;
        currency=MainActivity.currency_chosen;

        setTitle(price.getCrypto().getFullName()+" <-> "+currency);

        TextView cryptoLabel=(TextView)findViewById(R.id.cryptoCurrText);
        TextView currLabel=(TextView)findViewById(R.id.selectedCurrText);
        crypto=(EditText)findViewById(R.id.cryptoEdit);
        curr=(EditText)findViewById(R.id.currEdit);
        convert=(Button)findViewById(R.id.convert);

        cryptoLabel.setText(price.getCrypto().getShortName());
        currLabel.setText(price.getCurrencies().get(currency).getSymbol());

        crypto.setHint("Enter Crypto Amount to Convert");
        curr.setHint("Enter Local Currency Amount to convert");

        crypto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==true){
                    curr.setText("");
                    curr.setHint("Wait for RESULTS HERE");
                    crypto.setHint("Enter Crypto Amount to Convert");
                }
            }
        });

        curr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==true){
                    crypto.setText("");
                    crypto.setHint("Wait for RESULTS HERE");
                    curr.setHint("Enter Local Currency Amount to convert");
                }
            }
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when user enters local currency
                if(curr.hasFocus()==true){
                    double result=Double.parseDouble(curr.getText().toString());
                    result=result/price.getCurrencies().get(currency).getPrice_to_coin();
                    Log.e("Result",result+"");
                    crypto.setText(result+"");
                }

                //when user enters crypto currency
                if(crypto.hasFocus()==true){
                    double result=Double.parseDouble(crypto.getText().toString())*price.getCurrencies().get(currency).getPrice_to_coin();
                    Log.e("Result",result+"");
                    curr.setText(result+"");
                }
            }
        });
    }
}
