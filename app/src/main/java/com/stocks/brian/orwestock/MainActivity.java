package com.stocks.brian.orwestock;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import stocks.model.Coin;
import stocks.model.CoinsHolder;
import stocks.model.Currency;
import stocks.model.Price;

public class MainActivity extends AppCompatActivity {


    RVAdapter rv;
    RecyclerView recycler;
    NetWork network;
    boolean loaded=false;
    static int chosen_Search=-1;

    static Price price_chosen;
    static String currency_chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler=(RecyclerView)findViewById(R.id.recycler);
        LinearLayoutManager llm=new LinearLayoutManager(MainActivity.this);
        recycler.setLayoutManager(llm);

        rv=new RVAdapter(MainActivity.this,CoinsHolder.getPrices());
        recycler.setAdapter(rv);

        network=new NetWork();
        network.execute();
    }

    class RVAdapter extends RecyclerView.Adapter<RVAdapter.StockViewHolder>{

        Set<Price> prices;
        Context context;
        RVAdapter(Context context, Set<Price> prices){
            this.context=context;
            this.prices=prices;
        }

        @Override
        public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.stock,parent,false);
            StockViewHolder stc=new StockViewHolder(v);
            return stc;
        }

        @Override
        public void onBindViewHolder(final StockViewHolder holder, int position) {
            Iterator<Price> it=prices.iterator();
            Price price_to_use=null;

            boolean found=false;
            int pos=0;
            while(found==false){
                if(pos==position){
                    found=true;
                    price_to_use=it.next();
                }else{
                    it.next();
                }
                pos++;
            }

            ArrayList<String> countries_for_spinner=new ArrayList<>();
            for(int i=0;i<price_to_use.getCountries().size();i++){
                Currency currency=null;
                String country=price_to_use.getCountries().get(i);
                if((currency=price_to_use.getCurrencies().get(country))!=null){
                    countries_for_spinner.add(currency.getCountry());
                }
            }

            holder.text.setText(price_to_use.getCrypto().getFullName());
            ArrayAdapter<String> countries=new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_dropdown_item,
                    countries_for_spinner);
            holder.currenccies.setAdapter(countries);
            final Price finalPrice_to_use = price_to_use;
            holder.currenccies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String symbol_list="USD,EUR,JPY,GBP,AUD,CAD,CHF,CNY,SEK,NZD,MXN,SGD,HKD,KRW,RUB,INR,BRL,NGN,KES,ZAR";
                    String symbols[]=symbol_list.split(",");

                    Currency currency = finalPrice_to_use.getCurrencies().get(
                            finalPrice_to_use.getCountries().get(position)
                    );

                    if(currency!=null){
                        Log.e("Selected:",currency.toString());
                        float ratio=(float)currency.getPrice_to_coin();
                        String point=String.format(" %.5f",ratio);
                        holder.price.setText(currency.getSymbol()+" :"+point);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //On clicking CardView
            final Price finalPrice_to_use2 = price_to_use;
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get data
                    try{
                        price_chosen= finalPrice_to_use2;
                        currency_chosen=holder.currenccies.getSelectedItem().toString();
                        Intent calc=new Intent(MainActivity.this,CalculateActivity.class);
                        startActivity(calc);
                    }catch(Exception e){
                        Log.e("Error StartingCalc:",e.getMessage());
                    }
                }
            });

            //To load images
            final Price finalPrice_to_use1 = price_to_use;
            Picasso.with(context)
                    .load("https://www.cryptocompare.com"+finalPrice_to_use.getCrypto().getImage())
                    .resize(200,200)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop().into(holder.stockPick);
        }

        @Override
        public int getItemCount() {
            return prices.size();
        }



        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class StockViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView text;
            Spinner currenccies;
            TextView price;
            ImageView stockPick;

            public StockViewHolder(View itemView) {
                super(itemView);
                cv=(CardView)itemView.findViewById(R.id.cv);
                text =(TextView)itemView.findViewById(R.id.textTest);
                currenccies=(Spinner) itemView.findViewById(R.id.spinner_currencies);
                price=(TextView)itemView.findViewById(R.id.cur_price);
                stockPick=(ImageView)itemView.findViewById(R.id.stock_pic);
            }
        }
    }

    class NetWork extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        boolean internet_exists=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Checking Internet");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //check internet connection
            try {
                InetAddress net=InetAddress.getByName("google.com");
                if(net!=null){
                    internet_exists=true;
                }else{
                    internet_exists=false;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("InternetError",e.toString());
                internet_exists=false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("InternetError",e.toString());
                internet_exists=false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();

            if(internet_exists==true){
                Work work=new Work();
                work.execute();
            }else{
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("It Appears you have no internet\n" +
                        "Try check your connections and get one running");
                alert.setTitle("No Internet Detected");
                alert.setPositiveButton("Okay, I will check and come back later",null);
                alert.create().show();
            }
        }

        class Work extends AsyncTask<Void,Void,Void>{
            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress=new ProgressDialog(MainActivity.this);
                progress.setTitle("Getting Cyrpto Coins");
                progress.setMessage("Please wait");
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                CoinsHolder.loadCoins();
                Log.e("Done","Done with the shit");
                loaded=true;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                rv.notifyDataSetChanged();
                progress.hide();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==2){
            if(chosen_Search!=-1){
                recycler.getLayoutManager().scrollToPosition(chosen_Search);
            }else{
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("No such item found, please retry with another");
                alert.setPositiveButton("OKAY, I UNDERSTAND",null);
                alert.create().show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.exitMain){
            System.exit(0);
        }

        if(item.getItemId()==R.id.search){
            if(CoinsHolder.getPrices().size()<1){
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Hmmm...\nNo coins read, please REFRESH");
                alert.setPositiveButton("Okay, I Will",null);
                alert.create().show();
            }else{
                Intent i=new Intent(MainActivity.this,SearchActivity.class);
                startActivityForResult(i,2);
            }
        }

        if(item.getItemId()==R.id.refresh_menu){
            Log.e("Refresh Clicked","Clicked Refresh");
            network=new NetWork();
            network.execute();
        }

        if(item.getTitle().toString().contains("BTC")){
            Log.e("BTC clicked","Clicked BTC");
            if(recycler.getLayoutManager().getItemCount()>0){
                int pos=-1;

                Iterator<Price> price=CoinsHolder.getPrices().iterator();
                int curr_pos=0;
                boolean found=false;
                while(price.hasNext() && found==false){
                    Price p=price.next();
                    if(p.getCrypto().getShortName().equals("BTC")){
                        pos=curr_pos;
                        found=true;
                        Log.e("Found at: ",""+pos);
                    }
                    curr_pos++;
                }

                if(pos!=-1){
                    recycler.getLayoutManager().scrollToPosition(pos);
                }
            }else{
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Hmmm...\nNo coins read, please REFRESH");
                alert.setPositiveButton("Okay, I Will",null);
                alert.create().show();
            }
        }

        if(item.getTitle().toString().contains("ETH")){
            Log.e("ETH clicked","Clicked ETH");
            if(recycler.getLayoutManager().getItemCount()>0){
                int pos=-1;

                Iterator<Price> price=CoinsHolder.getPrices().iterator();
                int curr_pos=0;
                boolean found=false;
                while(price.hasNext() && found==false){
                    Price p=price.next();
                    if(p.getCrypto().getShortName().equals("ETH")){
                        pos=curr_pos;
                        found=true;
                        Log.e("Found at: ",""+pos);
                    }
                    curr_pos++;
                }

                if(pos!=-1){
                    recycler.getLayoutManager().scrollToPosition(pos);
                }
            }else{
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                alert.setMessage("Hmmm...\nNo coins read, please REFRESH");
                alert.setPositiveButton("Okay, I Will",null);
                alert.create().show();
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate=getMenuInflater();
        inflate.inflate(R.menu.options_menu,menu);
        return true;
    }
}
