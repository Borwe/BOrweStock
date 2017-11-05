package stocks.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by Brian on 10/12/2017.
 */

public class Coin implements Serializable{
    private String id;
    private String url;
    private String image;
    private String shortName;
    private String name;
    private String fullName;
    private String algorythmn;
    private String proofType;
    private String sortOrder;
    private Bitmap picture=null;

    public Coin(String id, String url, final String image, String shortName, String name, String fullName,
                String algorythmn, String proofType, String sortOrder){
        this.id=id;
        this.url=url;
        this.image=image;
        this.shortName=shortName;
        this.name=name;
        this.fullName=fullName;
        this.algorythmn=algorythmn;
        this.proofType=proofType;
        this.sortOrder=sortOrder;

        //Load Pictures;

    }

    public Bitmap getPicture() {
        if(picture==null){

            try {
                URL url_pic=new URL("https://www.cryptocompare.com"+image);
                InputStream readImage= null;
                readImage = url_pic.openStream();
                picture= BitmapFactory.decodeStream(readImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAlgorythmn() {
        return algorythmn;
    }

    public void setAlgorythmn(String algorythmn) {
        this.algorythmn = algorythmn;
    }

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
