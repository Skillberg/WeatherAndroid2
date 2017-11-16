package com.skillberg.weather2.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ForecastWind implements Parcelable {

    private float speed;

    @SerializedName("deg")
    private float degree;

    public ForecastWind() {
    }

    protected ForecastWind(Parcel in) {
        speed = in.readFloat();
        degree = in.readFloat();
    }

    public static final Creator<ForecastWind> CREATOR = new Creator<ForecastWind>() {
        @Override
        public ForecastWind createFromParcel(Parcel in) {
            return new ForecastWind(in);
        }

        @Override
        public ForecastWind[] newArray(int size) {
            return new ForecastWind[size];
        }
    };

    public float getSpeed() {
        return speed;
    }

    public float getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return "ForecastWind{" +
                "speed=" + speed +
                ", degree=" + degree +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(speed);
        dest.writeFloat(degree);
    }
}
