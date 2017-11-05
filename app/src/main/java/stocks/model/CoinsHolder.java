package stocks.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Brian on 10/12/2017.
 */

public class CoinsHolder {

    static ArrayList<Coin> coins=new ArrayList<>();
    static Set<Price> prices=new TreeSet<>();

    public static ArrayList<Coin> getCoins() {
        return coins;
    }

    public static Set<Price> getPrices() {
        return prices;
    }

    public static void loadCoins(){
        if(coins!=null){
            coins.clear();
        }else{
            coins=new ArrayList<>();
        }

        try {
            URL coinsList=new URL("https://www.cryptocompare.com/api/data/coinlist/");
            URLConnection connect=coinsList.openConnection();

            InputStream in=connect.getInputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(in));

            StringBuilder stringBuild=new StringBuilder();
            String data;

            while((data=reader.readLine())!=null){
                stringBuild.append(data);
            }

            parseJSON(stringBuild.toString());
            refreshCurrency();
        } catch (java.io.IOException e) {
            Log.e("Error",e.toString());
        } catch (InterruptedException e) {
            Log.e("Error",e.toString());
        }
    }

    private static void parseJSON(String s) {
        try {
            JSONObject full=new JSONObject(s);

            if(full.get("Response").equals("Success")){
                JSONObject data=full.getJSONObject("Data");


                Iterator<String> keys = data.keys();
                while(keys.hasNext()){
                    JSONObject coin=data.getJSONObject(keys.next());

                    if(coin.has("Name")){
                        Coin coinData;
                        if(!coin.has("ImageUrl")){
                            coinData=new Coin(coin.getString("Id"),
                                    coin.getString("Url"),"None",
                                    coin.getString("Symbol"),coin.getString("CoinName"),coin.getString("FullName"),
                                    coin.getString("Algorithm"),coin.getString("ProofType"),
                                    coin.getString("SortOrder"));
                        }else{
                            coinData=new Coin(coin.getString("Id"),
                                    coin.getString("Url"),coin.getString("ImageUrl"),
                                    coin.getString("Symbol"),coin.getString("CoinName"),coin.getString("FullName"),
                                    coin.getString("Algorithm"),coin.getString("ProofType"),
                                    coin.getString("SortOrder"));
                        }

                        coins.add(coinData);
                    }
                }

                Log.e("Coins Read",coins.size()+"hmm");
            }

        } catch (JSONException e) {
            Log.e("Error",e.getMessage());
        }
    }

    public static void refreshCurrency() throws InterruptedException {
        if(prices.size()>0){
            prices.clear();
        }

        int threads_parser=100;
        int numberOfThreads=coins.size()/threads_parser + 1;

        ArrayList<Thread> threads=new ArrayList<>();

        class Run implements Runnable{
            int start;
            int finish;

            Run(int start,int finish){
                this.start=start;
                this.finish=finish;
            }

            @Override
            public void run() {
                while(start!=finish){
                    if(start<coins.size()){
                        Price p=new Price(coins.get(start));
                        prices.add(p);
                    }
                    start++;
                }
            }
        }

        for(int i=0;i<numberOfThreads;i++){
            int start=i*threads_parser;
            int end=((i+1)*threads_parser);
            threads.add(new Thread(new Run(start,end)));
            threads.get(threads.size()-1).start();
        }

        for(Thread t:threads){
            if(t.isAlive()){
                t.join();
            }
        }

    }
}
