package com.skillberg.weather2.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ForecastMain implements Parcelable {

    private float temp;

    private float pressure;

    private float humidity;

    @SerializedName("temp_min")
    private float minTemp;

    @SerializedName("temp_max")
    private float maxTemp;

    public ForecastMain() {
    }

    protected ForecastMain(Parcel in) {
        temp = in.readFloat();
        pressure = in.readFloat();
        humidity = in.readFloat();
        minTemp = in.readFloat();
        maxTemp = in.readFloat();
    }

    public static final Creator<ForecastMain> CREATOR = new Creator<ForecastMain>() {
        @Override
        public ForecastMain createFromParcel(Parcel in) {
            return new ForecastMain(in);
        }

        @Override
        public ForecastMain[] newArray(int size) {
            return new ForecastMain[size];
        }
    };

    public float getTemp() {
        return temp;
    }

    public float getPressure() {
        return pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    @Override
    public String toString() {
        return "ForecastMain{" +
                "temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(temp);
        dest.writeFloat(pressure);
        dest.writeFloat(humidity);
        dest.writeFloat(minTemp);
        dest.writeFloat(maxTemp);
    }
}