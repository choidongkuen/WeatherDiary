package com.example.weatherdiary.service;

import com.example.weatherdiary.WeatherDiaryApplication;
import com.example.weatherdiary.domain.entity.DateWeather;
import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.domain.repository.DateWeatherRepository;
import com.example.weatherdiary.domain.repository.DiaryRepository;
import com.example.weatherdiary.dto.CreateDiaryRequestDto;
import com.example.weatherdiary.dto.DateInfo;
import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import com.example.weatherdiary.exception.NotFoundDiaryException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class 에 @Transactional 사용 가능 -> 모든 메소드에 적용
// Class 에도 붙어있고 메소드에도 붙어있으면 메소드를 우선순위 적용

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherDiaryApplication.class);

    @Value("${openweathermap.apiKey}")
    private String apiKey;

    @Value("${openweathermap.city}")
    private String city;


    @Transactional
    public void createDiary(LocalDate date, CreateDiaryRequestDto request) throws IOException {

        logger.info("start to create diary");

        DateWeather dateWeather = getDateWeather(date);

        diaryRepository.save(
                Diary.builder()
                        .weather(dateWeather.getWeather())
                        .icon(dateWeather.getIcon())
                        .temperature(dateWeather.getTemperature())
                        .title(request.getTitle())
                        .text(request.getText())
                        .date(date)
                        .build());

        logger.info("end to create diary");

    }

    private DateWeather getDateWeather(LocalDate date) throws IOException {

        List<DateWeather> dateWeatherListFromDB
                = dateWeatherRepository.findAllByDate(date); // DB 에서 먼저 가져오기

        if(0 == dateWeatherListFromDB.size()) {
            return getWeatherFromApi();
        } else { // 없다면 API 호출
            return dateWeatherListFromDB.get(0);
        }
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
            logger.error("failed to get response");

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

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherData = (JSONArray) jsonObject.get("weather");
        JSONObject weatherDataObj = (JSONObject) weatherData.get(0);
        resultMap.put("main", weatherDataObj.get("main"));
        resultMap.put("icon", weatherDataObj.get("icon"));
        return resultMap;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 매 오전 1시마다 데이터 API 호출해서 데이터 저장
    public void saveWeatherDate() throws IOException {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi() throws IOException {

        String weatherData = getWeatherString();
        Map<String, Object> parseWeather = parseWeather(weatherData);

        return DateWeather.of(parseWeather);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {

        logger.debug("start to read diary");

        List<Diary> diaryList = diaryRepository.findAllByDate(date);
        checkException(diaryList);

        logger.debug("end to read diary");

        return diaryList;
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries(DateInfo request) {

        List<Diary> diaryList
                = diaryRepository.findAllByDateBetween(request.getStartDate(), request.getEndDate());

        checkException(diaryList);

        return diaryList;
    }

    private static void checkException(List<Diary> diaryList) {
        if (0 == diaryList.size()) {
            throw new NotFoundDiaryException("일치하는 일기 데이터가 존재하지 않습니다.");
        }
    }

    @Transactional
    public void modifyDiary(ModifyDiaryRequestDto request,
                            Long id) {

        Diary diary = diaryRepository.findById(id)
                                     .orElseThrow(() -> new NotFoundDiaryException("일치하는 일기 데이터가 존재하지 않습니다."));

        diary.update(request);
    }

    @Transactional
    public void deleteDiary(Long id) {

        Diary diary = diaryRepository.findById(id)
                                     .orElseThrow(() -> new NotFoundDiaryException("일치하는 일기 데이터가 존재하지 않습니다."));

        diaryRepository.delete(diary);
    }
}
