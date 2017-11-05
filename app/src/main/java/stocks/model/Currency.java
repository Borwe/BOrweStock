package stocks.model;

/**
 * Created by Brian on 10/12/2017.
 */

public class Currency {

    private String symbol;
    private String country;
    private double price_to_coin;

    Currency(String country,String symbol,double price_to_coin){
        this.country=country;
        this.symbol=symbol;
        this.price_to_coin=price_to_coin;
    }

    public String getCountry() {
        return country;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice_to_coin() {
        return price_to_coin;
    }

    @Override
    public String toString() {
        return symbol+" : "+price_to_coin;
    }
}
