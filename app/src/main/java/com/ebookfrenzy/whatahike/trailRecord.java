package com.ebookfrenzy.whatahike;

public class trailRecord {
    public int image;
    public String trailName;
    public String information;

    public trailRecord(int image, String trailName, String information){
        this.image = image;
        this.trailName = trailName;
        this.information = information;
    }

    public int getImage(){
        return image;
    }

    public void setImage(){
        this.image = image;
    }

    public String getInformation(){
        return information;
    }

    public void setInformation(){
        this.information = information;
    }

    public void setTrailName(){
        this.trailName = trailName;
    }

    public String getTrailName() {
        return trailName;
    }
}
