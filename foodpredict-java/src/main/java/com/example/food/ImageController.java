package com.example.food;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ImageController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WineService wineService;

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {

        // 파이썬 서버 URL
        String pythonServerUrl = "http://localhost:5000/predict";

        // 저장할 디렉토리 경로
        String directoryPath = "C:/Users/Pang Choi/Desktop/FoodParing/images";

        try {
            // 파일 이름 로그 출력
            System.out.println("Uploading file: " + file.getOriginalFilename());

            // 파일을 로컬 디렉토리에 저장
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = directoryPath + "\\" + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);

            // 이미지 경로를 DB에 저장
            Image image = new Image();
            image.setImagePath(filePath);
            Image savedImage = imageRepository.save(image);

            // 이미지가 정상적으로 저장되었음을 로그로 출력
            System.out.println("Image saved to DB with ID: " + savedImage.getId());

            // FileSystemResource로 파일 경로 지정
            FileSystemResource imageResource = new FileSystemResource(dest);

            // HttpEntity 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", imageResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 요청 전송 로그 출력
            System.out.println("Sending request to Python server...");

            // 파이썬 서버로 POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(pythonServerUrl, requestEntity, String.class);

            // 응답 수신 로그 출력
            System.out.println("Received response from Python server: " + response.getStatusCode());

            // Python 서버로부터 받은 응답 본문 로그 출력
            System.out.println("Response body from Python server: " + response.getBody());

            // ObjectMapper 인스턴스 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // response.getBody()로부터 predictedClass 추출
            String jsonBody = response.getBody();
            Map<String, String> responseBodyMap = objectMapper.readValue(jsonBody, new TypeReference<Map<String,String>>() {});
            String predictedClass = responseBodyMap.get("predicted_class");

            // 예측된 음식과 페어링되는 와인 리스트 조회 로직 구현
            List<Wine> wines = wineService.findWinesByPairingFood(predictedClass);

            // 조회된 와인 정보 로그 출력
            System.out.println("Pairing wines:");
            for (Wine wine : wines) {
                System.out.println(wine);
            }
            // 조회된 와인 정보를 프론트엔드로 응답
            return new ResponseEntity<>(wines, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to process the image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

@Configuration
class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
