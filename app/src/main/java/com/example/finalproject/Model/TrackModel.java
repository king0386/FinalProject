package com.example.finalproject.Model;

public class TrackModel {
    private String idTrack;
    private String strTrack;
    private String strArtist;

    public TrackModel(String idTrack, String strTrack, String strArtist) {
        this.idTrack = idTrack;
        this.strTrack = strTrack;
        this.strArtist = strArtist;
    }

    public String getIdTrack() {
        return idTrack;
    }

    public String getStrTrack() {
        return strTrack;
    }

    public String getStrArtist() {
        return strArtist;
    }
}
