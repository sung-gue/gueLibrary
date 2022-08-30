package com.breakout.sample.openapi.openweathermap;

import android.content.Context;
import android.os.Handler;

import com.breakout.sample.controller.ControllerEx;
import com.breakout.sample.controller.ControllerType;
import com.breakout.util.net.HttpMethod;


/**
 * Open Weather open api
 * https://openweathermap.org
 *
 * @author sung-gue
 * @version 1.0 (2021-10-01)
 */
public class WeatherController extends ControllerEx<WeatherDto> {
    private static final ControllerType _nettype = ControllerType.Weather;

    public WeatherController(Context context, Handler handler) {
        super(context, handler, _nettype.getApiUrl());
        super.setCheckNetState(true);
    }

    public WeatherController(Context context, Handler handler, boolean isDialogSkip) {
        this(context, handler);
        setErrorDialogSkip(isDialogSkip);
    }

    /**
     * https://openweathermap.org/current#geo
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
    public void getCurrentWeather() {
        super.setRequiredParam("/data/2.5/weather");

        setParamAfterNullCheck("lat", "37.388109");
        setParamAfterNullCheck("lon", "126.634928");
        setParamAfterNullCheck("appid", "6868e3dcbba93f515073950880a84637");
        setParamAfterNullCheck("mode", "json");
        setParamAfterNullCheck("units", "metric");
        setParamAfterNullCheck("lang", "kr");
        startRequest(HttpMethod.GET);
    }

    @Override
    protected WeatherDto initObject() {
        return new WeatherDto();
    }

    @Override
    protected WeatherDto parsing(String responseStr) throws Exception {
        return _nettype.getParseObject(responseStr);
    }

    @Override
    protected void urlDecode(WeatherDto dto) {
        super.urlDecode(dto);
//        dto.welcomeMessage = urlDecoder(dto.welcomeMessage);
    }
}
