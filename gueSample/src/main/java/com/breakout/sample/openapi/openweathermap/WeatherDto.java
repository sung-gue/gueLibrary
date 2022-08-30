package com.breakout.sample.openapi.openweathermap;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.breakout.sample.dto.BaseDto;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <pre>
 *     {
 *     "coord": {
 *         "lon": 126.6349,
 *         "lat": 37.3881
 *     },
 *     "weather": [
 *         {
 *             "id": 803,
 *             "main": "Clouds",
 *             "description": "튼구름",
 *             "icon": "04d"
 *         }
 *     ],
 *     "base": "stations",
 *     "main": {
 *         "temp": 20.29,
 *         "feels_like": 20.39,
 *         "temp_min": 20.05,
 *         "temp_max": 23.02,
 *         "pressure": 1016,
 *         "humidity": 77
 *     },
 *     "visibility": 10000,
 *     "wind": {
 *         "speed": 4.63,
 *         "deg": 280
 *     },
 *     "clouds": {
 *         "all": 75
 *     },
 *     "dt": 1633594430,
 *     "sys": {
 *         "type": 1,
 *         "id": 8093,
 *         "country": "KR",
 *         "sunrise": 1633556023,
 *         "sunset": 1633597724
 *     },
 *     "timezone": 32400,
 *     "id": 1843564,
 *     "name": "Incheon",
 *     "cod": 200
 * }
 * </pre>
 */
public class WeatherDto extends BaseDto<WeatherDto> {

    @SerializedName("coord")
    public Bundle coord;

    @SerializedName("weather")
    public ArrayList<Weather> weathers;

    @SerializedName("base")
    public String base;

    @SerializedName("main")
    public Main main;

    @SerializedName("wind")
    public Bundle wind;

    public WeatherDto() {
        super();
        weathers = new ArrayList<>();
        main = new Main();
    }

    public WeatherDto(Parcel in) {
        super(in);
        coord = in.readBundle(HashMap.class.getClassLoader());
        weathers = new ArrayList<>();
        in.readTypedList(weathers, Weather.CREATOR);
        base = in.readString();
        main = in.readParcelable(Main.class.getClassLoader());
        wind = in.readBundle(HashMap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeBundle(coord);
        dest.writeTypedList(weathers);
        dest.writeString(base);
        dest.writeParcelable(main, 0);
        dest.writeBundle(wind);
    }

    @Override
    protected ClassLoader getDataClassLoader() {
        return WeatherDto.class.getClassLoader();
    }

    @Override
    protected Parcelable.Creator<WeatherDto> getDataCreator() {
        return WeatherDto.CREATOR;
    }

    public static final Parcelable.Creator<WeatherDto> CREATOR = new Parcelable.Creator<WeatherDto>() {
        @Override
        public WeatherDto createFromParcel(Parcel source) {
            return new WeatherDto(source);
        }

        @Override
        public WeatherDto[] newArray(int size) {
            return new WeatherDto[size];
        }
    };


    /**
     * <pre>
     *     {
     *     "weather": [
     *         {
     *             "id": 803,
     *             "main": "Clouds",
     *             "description": "튼구름",
     *             "icon": "04d"
     *         }
     *     ],
     * }
     * </pre>
     */
    public static class Weather implements Parcelable {
        @SerializedName("id")
        public String id;

        @SerializedName("main")
        public String main;

        @SerializedName("description")
        public String description;

        @SerializedName("icon")
        public String icon;

        public Weather() {
        }

        public Weather(Parcel in) {
            id = in.readString();
            main = in.readString();
            description = in.readString();
            icon = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(main);
            dest.writeString(description);
            dest.writeString(icon);
        }

        public static final Creator<Weather> CREATOR = new Creator<Weather>() {
            @Override
            public Weather createFromParcel(Parcel in) {
                return new Weather(in);
            }

            @Override
            public Weather[] newArray(int size) {
                return new Weather[size];
            }
        };
    }


    /**
     * <pre>
     *     {
     *     "main": {
     *         "temp": 20.29,
     *         "feels_like": 20.39,
     *         "temp_min": 20.05,
     *         "temp_max": 23.02,
     *         "pressure": 1016,
     *         "humidity": 77
     *     },
     * }
     * </pre>
     */
    public static class Main implements Parcelable {
        @SerializedName("temp")
        public float temp;
        @SerializedName("feels_like")
        public float feelsLike;
        @SerializedName("temp_min")
        public float tempMin;
        @SerializedName("temp_max")
        public float tempMax;
        @SerializedName("pressure")
        public float pressure;
        @SerializedName("humidity")
        public float humidity;

        public Main() {
        }

        public Main(Parcel in) {
            temp = in.readFloat();
            feelsLike = in.readFloat();
            tempMin = in.readFloat();
            tempMax = in.readFloat();
            pressure = in.readFloat();
            humidity = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(temp);
            dest.writeFloat(feelsLike);
            dest.writeFloat(tempMin);
            dest.writeFloat(tempMax);
            dest.writeFloat(pressure);
            dest.writeFloat(humidity);
        }

        public static final Creator<Main> CREATOR = new Creator<Main>() {
            @Override
            public Main createFromParcel(Parcel in) {
                return new Main(in);
            }

            @Override
            public Main[] newArray(int size) {
                return new Main[size];
            }
        };
    }
}
