package com.dab.medireminder.data.model;

public class Advisory {
    private String content;
    private String image;
    private long time;
    private boolean showMore;

    public Advisory() {

    }

    public Advisory(String content, long time, String image) {
        this.content = content;
        this.time = time;
        this.image = image;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isShowMore() {
        return showMore;
    }

    public void setShowMore(boolean showMore) {
        this.showMore = showMore;
    }
}
