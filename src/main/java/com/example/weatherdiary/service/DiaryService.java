package com.example.weatherdiary.service;

import com.example.weatherdiary.domain.entity.DateWeather;
import com.example.weatherdiary.domain.entity.Diary;
import com.example.weatherdiary.domain.repository.DateWeatherRepository;
import com.example.weatherdiary.domain.repository.DiaryRepository;
import com.example.weatherdiary.dto.CreateDiaryRequestDto;
import com.example.weatherdiary.dto.DateInfo;
import com.example.weatherdiary.dto.ModifyDiaryRequestDto;
import com.example.weatherdiary.exception.NotFoundDiaryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    @Value("${openweathermap.apiKey}")
    private String apiKey;

    @Value("${openweathermap.city}")
    private String city;


    @Transactional
    public void createDiary(LocalDate date, CreateDiaryRequestDto request) throws IOException {

        String weatherData = getWeatherString();
        Map<String, Object> parseWeather = parseWeather(weatherData);

        diaryRepository.save(Diary.builder()
                                  .weather(parseWeather.get("main").toString())
                                  .temperature((Double) parseWeather.get("temp"))
                                  .icon(parseWeather.get("icon").toString())
                                  .text(request.getText())
                                  .title(request.getTitle())
                                  .date(date)
                                  .build()
        );
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
    @Scheduled(cron = "0/5 0 1 * * *")
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

        List<Diary> diaryList = diaryRepository.findAllByDate(date);
        checkException(diaryList);

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
