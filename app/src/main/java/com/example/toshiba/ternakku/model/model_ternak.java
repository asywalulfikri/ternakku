package com.example.toshiba.ternakku.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Toshiba on 12/11/2015.
 */
public class model_ternak  implements Serializable {

    private String iduser;
    private String idhewan;
    private String idh;
    public static String urlternak;
    private String nama;
    private String jenis;
    private String status;
    private String jekel;
    private Date tgllahir;
    private String weight;
    private String umur;

    private String kandang;

    String foto;

    public String getIdh() {
        return idh;
    }
    public void setIdh(String idh) {
        this.idh = idh;
    }

    public String getIdhewan() {
        return idhewan;
    }
    public void setIdhewan(String idhewan) {
        this.idhewan = idhewan;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }
    public String getFoto() {
        return foto;
    }

    public String getUmur() {
        return umur;
    }
    public void setUmur(String umur) {
        this.umur = umur;
    }
    public String getKandang() {
        return kandang;
    }
    public void setKandang(String kandang) {
        this.kandang = kandang;
    }
    public String getJekel() {
        return jekel;
    }
    public void setJekel(String jekel) {
        this.jekel = jekel;
    }
    public Date getTgllahir() {
        return tgllahir;
    }
    public void setTgllahir(Date tgllahir) {
        this.tgllahir = tgllahir;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String berat) {
        this.weight = berat;
    }

    public String getidUser(){
        return iduser;
    }
    public void setidUser(String iduser){
        this.iduser = iduser;
    }
    public String geturlternak() {
        return urlternak;
    }
    public void seturlternak(String urlternak) {
        this.urlternak = urlternak;
    }
    public String getNama(){
        return nama;
    }
    public void setNama(String nama){
        this.nama = nama;
    }
    public String getJenis(){
        return jenis;
    }
    public void setJenis(String jenis){
        this.jenis = jenis;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }


    public static List<model_ternak> parseFeed(String response) {
        try {

            JSONObject obj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray arrayObj = obj.getJSONArray("data");

            List<model_ternak> ternakList = new ArrayList<model_ternak>();

            for (int i = 0; i < arrayObj.length(); i++) {
                JSONObject ternakObj = arrayObj.getJSONObject(i);
                model_ternak ternak = new model_ternak();

                ternak.setidUser(ternakObj.getString("iduser"));
                ternak.setIdhewan(ternakObj.getString("idhewan"));
                ternak.setIdh(ternakObj.getString("idh"));
                ternak.setNama(ternakObj.getString("nama"));
                ternak.setJekel(ternakObj.getString("jekel"));
                ternak.setUmur(ternakObj.getString("umur"));
                ternak.setWeight(ternakObj.getString("weight"));
                ternak.setKandang(ternakObj.getString("kandang"));
                ternak.setFoto(ternakObj.getString("foto"));
                ternak.setStatus(ternakObj.getString("status"));
                ternak.seturlternak(ternakObj.getString("url"));


                ternakList.add(ternak);
                Log.d("idh", ternak.getIdh());
            }
            return ternakList;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

}
