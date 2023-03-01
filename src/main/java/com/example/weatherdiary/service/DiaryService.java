package com.example.weatherdiary.service;

import com.example.weatherdiary.domain.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Value("${openweathermap.apiKey}")
    private String apiKey;

    @Value("${openweathermap.city}")
    private String city;

    public String createDiary(LocalDate date, String text) throws IOException {

        String weatherData = getWeatherString();
        Map<String, Object> parseWeather = parseWeather(weatherData);

        return "";
    }


    private String getWeatherString() throws IOException { // openweathermap api 호출 데이터 가져오기

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode(); // 응답 코드 받기
            BufferedReader br;

            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            log.error("failed to get response");

            return "Failed";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) { // openweathermap 데이터 파싱

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;


        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String,Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));
        JSONObject weatherData = (JSONObject) jsonObject.get("weather");
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon",weatherData.get("icon"));
        return resultMap;
    }
}
