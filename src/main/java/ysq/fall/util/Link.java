package ysq.fall.util;

import java.io.Serializable;

public class Link implements Serializable {
    private String url;
    private String text;
    private Integer count;
    private String tip;
    private Boolean disable;

    public Link(){
        
    }

    public Link(String text, String url){
        this.text=text;
        this.url=url;
        this.disable=false;
    }

    public Link(String text, String url, Boolean disable){
        this.text=text;
        this.url=url;
        this.disable=disable;
    }

    /**
     *
     * @param text
     * @param url
     * @param count
     */
    public Link(String text, String url, Integer count){
        this.text=text;
        this.url=url;
        this.count=count;
    }

    // ------------------------------------

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }
}
