package com.skillberg.weather2.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Погода в данный момент
 */
public class CurrentWeather implements Parcelable {

    private ForecastMain main;

    private ForecastWind wind;

    @SerializedName("name")
    private String cityName;

    public CurrentWeather() {
    }

    protected CurrentWeather(Parcel in) {
        main = in.readParcelable(ForecastMain.class.getClassLoader());
        wind = in.readParcelable(ForecastWind.class.getClassLoader());
        cityName = in.readString();
    }

    public static final Creator<CurrentWeather> CREATOR = new Creator<CurrentWeather>() {
        @Override
        public CurrentWeather createFromParcel(Parcel in) {
            return new CurrentWeather(in);
        }

        @Override
        public CurrentWeather[] newArray(int size) {
            return new CurrentWeather[size];
        }
    };

    public ForecastMain getMain() {
        return main;
    }

    public ForecastWind getWind() {
        return wind;
    }

    public String getCityName() {
        return cityName;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "main=" + main +
                ", wind=" + wind +
                ", cityName='" + cityName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(main, flags);
        dest.writeParcelable(wind, flags);
        dest.writeString(cityName);
    }
}
