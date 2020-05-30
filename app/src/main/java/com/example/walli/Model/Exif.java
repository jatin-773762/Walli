
package com.example.walli.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exif {

    @SerializedName("make")
    @Expose
    private Object make;
    @SerializedName("model")
    @Expose
    private Object model;
    @SerializedName("exposure_time")
    @Expose
    private Object exposureTime;
    @SerializedName("aperture")
    @Expose
    private Object aperture;
    @SerializedName("focal_length")
    @Expose
    private Object focalLength;
    @SerializedName("iso")
    @Expose
    private Object iso;

    public Object getMake() {
        return make;
    }

    public void setMake(Object make) {
        this.make = make;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public Object getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Object exposureTime) {
        this.exposureTime = exposureTime;
    }

    public Object getAperture() {
        return aperture;
    }

    public void setAperture(Object aperture) {
        this.aperture = aperture;
    }

    public Object getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(Object focalLength) {
        this.focalLength = focalLength;
    }

    public Object getIso() {
        return iso;
    }

    public void setIso(Object iso) {
        this.iso = iso;
    }

}
