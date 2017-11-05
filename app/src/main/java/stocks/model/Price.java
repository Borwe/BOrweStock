package stocks.model;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Brian on 10/12/2017.
 */

public class Price implements  Comparable<Price>, Serializable{

    private String url="https://min-api.cryptocompare.com/data/price?fsym=";

    private Coin crypto;

    private HashMap<String,Currency> currencies;
    private ArrayList<String> countries;

    public Price(Coin c){
        currencies=new HashMap<>();
        crypto=c;
        url+=crypto.getShortName()+"&tsyms=USD,EUR,JPY,GBP,AUD,CAD,CHF,CNY,SEK,NZD,MXN,SGD,HKD,KRW,RUB,INR,BRL,NGN,KES,ZAR";
        countries=new ArrayList<>();
        countries.add("USA Dolar");
        countries.add("Euro");
        countries.add("Japanese Yen");
        countries.add("Pound Sterlng");
        countries.add("Australian Dolar");
        countries.add("Canadian Dolar");
        countries.add("Swiss Franc");
        countries.add("Renmindi");
        countries.add("Swedish Krona");
        countries.add("NewZealand Dolar");
        countries.add("Mexico Peso");
        countries.add("Singapore Dolar");
        countries.add("HongKong Dolar");
        countries.add("South Korean won");
        countries.add("Russian Rubble");
        countries.add("Indian Ruppe");
        countries.add("Brazilian Real");
        countries.add("Nigerian Naira");
        countries.add("Kenyan Shilling");
        countries.add("South African Rand");

        getJSONOfCurrencyPricing();
    }

    public void getJSONOfCurrencyPricing(){
        String web=url;
        try {
            URL url=new URL(web);

            URLConnection urlConnection=url.openConnection();
            urlConnection.setConnectTimeout(50);
            InputStream in=urlConnection.getInputStream();

            BufferedReader reader=new BufferedReader(new InputStreamReader(in));

            StringBuilder string=new StringBuilder();
            String data;
            while((data=reader.readLine())!=null){
                string.append(data);
            }

            parseJSON(string.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Log.e("Error on Price",e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error on Price",e.getMessage());
        }
    }

    private void parseJSON(String jsonString) throws JSONException {
        JSONObject page=new JSONObject(jsonString);

        Iterator<String> keys = page.keys();
        int position=0;
        while(keys.hasNext()){
            String keysString=keys.next();
            if(keysString.length()>=1){
                Currency c=new Currency(countries.get(position),keysString,page.getDouble(keysString));
                currencies.put(countries.get(position),c);
            }
            position++;
        }

        Log.e("Currencies",currencies.toString());
    }

    public Coin getCrypto() {
        return crypto;
    }

    public HashMap<String, Currency> getCurrencies() {
        return currencies;
    }

    @Override
    public String toString() {
        return crypto.getShortName();
    }

    @Override
    public int compareTo(@NonNull Price o) {
        return crypto.getShortName().compareTo(o.toString());
    }

    public ArrayList<String> getCountries() {
        return countries;
    }
}
