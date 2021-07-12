package com.woowacourse.s3proxy;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.woowacourse.s3proxy.common.FileFactory;
import com.woowacourse.s3proxy.config.StorageTestConfiguration;
import com.woowacourse.s3proxy.exception.ExceptionAdvice.ExceptionDto;
import com.woowacourse.s3proxy.web.presentation.Dto.Files;
import com.woowacourse.s3proxy.web.presentation.Dto.Files.Response;
import io.restassured.RestAssured;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSender;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@Import(StorageTestConfiguration.class)
@LocalstackDockerProperties(services = {"s3"}, platform = "linux/x86_64")
@ExtendWith(LocalstackDockerExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class S3ProxyAcceptanceTest {

    @Value("${aws.cloud_front.file_url_format}")
    private String fileUrlFormat;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("정상 이미지를 저장하고 주소를 반환받는다.- 성공")
    @Test
    void StoreRightImageAndReturnRrl_True() throws IOException {
        File file = FileFactory.getTestRightImage1File();

        Response result =
            Request.sendWithFile("files", file, Response.class);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new Files.Response(
                List.of(String.format(fileUrlFormat, file.getName()))
            ));
    }

    @DisplayName("비정상 이미지를 차단하고 에러를 반환받는다.")
    @Test
    void StoreAbnormalImageAndReturnRrl_ExceptionThrown() throws IOException {
        File file = FileFactory.getTestFailImage1File();

        ExceptionDto result =
            Request.sendWithFile("files", file, ExceptionDto.class);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new ExceptionDto("업로드 실패"));
    }

    @DisplayName("정상 이미지들을 저장하고 주소를 반환받는다. - 성공")
    @Test
    void StoreNormalImagesAndReturnRrl_True() throws IOException {
        //given
        List<File> files = List.of(
            FileFactory.getTestRightImage1File(),
            FileFactory.getTestRightImage2File()
        );

        //when
        Response result =
            Request.sendWithFiles("files", files, Response.class);

        //then
        List<String> fileNames =
            files.stream()
                .map(file -> String.format(fileUrlFormat, file.getName()))
                .collect(toList());

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new Files.Response(fileNames));
    }

    @DisplayName("비정상 이미지들을 저장하고 에러를 반환받는다. - 실패")
    @Test
    void StoreAbnormalImagesAndReturnRrl_ExceptionThrown() throws IOException {
        List<File> files = List.of(
            FileFactory.getTestFailDataFile(),
            FileFactory.getTestFailImage1File()
        );

        ExceptionDto result =
            Request.sendWithFiles("files", files, ExceptionDto.class);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new ExceptionDto("업로드 실패"));
    }

    @DisplayName("비정상 이미지와 정상 이미지를 함게 저장하고 에러를 반환받는다. - 실패")
    @Test
    void StoreAbnormalAndNormalImagesAndReturnError_ExceptionThrown() throws IOException {
        List<File> files = List.of(
            FileFactory.getTestRightImage1File(),
            FileFactory.getTestRightImage2File(),
            FileFactory.getTestFailImage1File()
        );

        ExceptionDto result =
            Request.sendWithFiles("files", files, ExceptionDto.class);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(new ExceptionDto("업로드 실패"));
    }

    private static class Request {

        public static <T> T sendWithFiles(
            String controlName,
            List<File> multipartFile,
            Class<T> returnType
        ) {
            return footer(
                multiParts(controlName, multipartFile, multipartFile.size()),
                returnType
            );
        }

        public static <T> T sendWithFile(
            String controlName,
            File multipartFile,
            Class<T> returnType
        ) {
            return footer(
                multiParts(controlName, List.of(multipartFile), 1),
                returnType
            );
        }

        private static RequestSpecification multiParts(
            String controlName,
            List<File> files,
            int depth
        ) {
            if (depth == 0) {
                return header(controlName);
            }

            return multiParts(controlName, files, depth-1)
                .multiPart(controlName, files.get(depth-1));
        }

        private static RequestSpecification header(String controlName) {
            return RestAssured
                .given().log().all()
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        }

        private static <T> T footer(RequestSpecification request, Class<T> returnType) {
            return request.when()
                .post("/api/storage")
                .then().log().all()
                .extract().as(returnType);
        }

    }
}
