package com.example.finalproject;

/**
 * CovidSearchResult contains search result
 * @author Stewart King
 */
public class CovidSearchResult {

    private String country;
    private String province;
    private int caseNumber;
    private String date;
    private long id;
    /**
     * The default constructors
     */
    public CovidSearchResult(){

    }

    public CovidSearchResult( String c,String p,int ca,String d)
    {
        country=c;
        province=p;
        caseNumber=ca;
        date=d;
    }
    public CovidSearchResult( String p,int ca)
    {
        province=p;
        caseNumber=ca;
    }
    /**
     * Getters
     */
    public String getCountry() {
        return country;
    }
    public String getProvince() {
        return province;
    }
    public int getCase() {
        return caseNumber;
    }
    public String getDate() {
        return date;
    }
    public long getId() {
        return id;
    }
}
