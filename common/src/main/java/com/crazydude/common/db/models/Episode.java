package com.crazydude.common.db.models;

import com.crazydude.common.utils.Utils;

import io.realm.RealmObject;

/**
 * Created by Crazy on 10.01.2017.
 */

public class Episode extends RealmObject implements Utils.PosterProvider {

    private String id;
    private String name;
    private Season season;
    private int detailsId;
    private String posterUrl;
    private String episodeFilePath;
    private int fileDownloadPercentage;
    private boolean isDownloading;

    public Episode() {
    }

    public Episode(String id, String name, Season season, int detailsId, String posterUrl,
                   String episodeFilePath, int fileDownloadPercentage, boolean isDownloading) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.detailsId = detailsId;
        this.posterUrl = posterUrl;
        this.episodeFilePath = episodeFilePath;
        this.fileDownloadPercentage = fileDownloadPercentage;
        this.isDownloading = isDownloading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public int getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(int detailsId) {
        this.detailsId = detailsId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    @Override
    public String providePosterURL() {
        return Utils.generatePosterUrl(getSeason().getTvShow().getId(), getSeason().getId(),
                getId());
    }

    public String getEpisodeFilePath() {
        return episodeFilePath;
    }

    public void setEpisodeFilePath(String episodeFilePath) {
        this.episodeFilePath = episodeFilePath;
    }

    public int getFileDownloadPercentage() {
        return fileDownloadPercentage;
    }

    public void setFileDownloadPercentage(int fileDownloadPercentage) {
        this.fileDownloadPercentage = fileDownloadPercentage;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }
}
