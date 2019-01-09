package com.gings.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * 
 * 이미지 파일 관련 연산을 제공한다. 현재로는 파일 확장자 검사 메서드만 정의되어 있으나 후에 파일 압축 등의 메서드를 추가할까 고려중이다.
 * 
 * 현재 지상 어플리케이션의 마켓 정보 수정 과정의 동작은 다음과 같다. 클라이언트가 수정 화면에 대한 GET 요청을 하면 서버는 수정 화면에
 * 보여줄 수 있는 모든 정보를 클라이언트로 전달하며 수정을 위한 POST(MultipartFile은 PUT이 안됨.) 요청시 클라이언트는
 * 변경된 정보와 변경되지 않은 정보를 모두 서버로 전달한다. 이렇게 구현할 경우 서버에서 업데이트 작업시 동적 쿼리를 작성하지 않아도 된다는
 * 장점이 있다. 또한 더 중요한 이유는 클라이언트가 변경된 정보만 서버로 보내면, 변경된 정보에 대한 화면 표시를 위해 서버에서 데이터를
 * 다시 돌려줄 때 변경된 정보만 전달할텐데 (DB 접근 횟수를 최소화 하기 위해 - 변경 안 된 정보까지 반환하려면 업데이트 후 또 한번
 * 조회가 필요할 것이다.) 그럼 클라이언트는 화면을 구성하는 여러 요소 중 서버로 부터 전달된 요소(화면 변경이 필요한 정보)만 일일이 찾아
 * 해당 요소에 대한 화면만 변경하게 될 텐데 서버로서나 클라이언트로서나 둘다 불편하기만 하다.
 * 
 * 아무튼 서버는 클라이언트에 항상 화면에 필요한 모든 정보를 보내고 클라이언트는 항상 변경 여부를 떠나 서버가 전달한 요소를 서버에
 * 전송한다. 이렇게 구현하다보니 고민거리가 하나 생겼는데, 마켓 대표 이미지의 경우 사용자가 변경을 하지 않을 경우에도 클라이언트가 이미지를
 * 보내게하고 싶지 않았다. 구현이 보다 복잡해질 것이다. 그리하여 마켓 대표 이미지에 한해서는 변경이 이루어지지 않을 경우 서버로 전송하지
 * 않도록 하였는데, 그러다보니 컨트롤러의 핸들러 메서드 인자로 {@link MarketManagementDTO} 오브젝트를 바인딩할 때
 * validation 수행에 한계가 있었다. 결국 다른 layer에서 이미지 업로드 이전에 이미지 확장자를 검사하기로 결정하였고 이 클래스가
 * 그 역할을 담당하게 되었다.
 * 
 * 또한 상품 등록 등에 작업시 핸들러 메서드 파라미터 오브젝트에 대한 바인딩시 이미지 포맷 검증을 수행하는
 * {@link ImageExtensionValidator}에서 내부적으로 이 클래스의 {@link #validateImage(String)}
 * 메서드를 사용한다.
 * 
 * 
 * @author leeseunghyun
 *
 */
public class ImageOperationProvider {

    private static final Logger logger = 
                                LoggerFactory.getLogger(ImageOperationProvider.class);
    
    private static final List<String> supportedExtensions = new ArrayList<String>() {

        private static final long serialVersionUID = -6913419777268438554L;

        {
            add(".jpg");
            add(".JPG");
            add(".jpeg");
            add(".JPEG");
            add(".png");
            add(".PNG");
            add(".tif");
            add(".tiff");
        }
    };

    private static final String IMAGE_FIELD = "image";

    public static void validateImage(String filename) throws Throwable {

        logger.debug("Starting image file name validation.");

        if (StringUtils.isEmpty(filename)) {
            logger.info("Received empty String type argument filename while trying to validate image.");
            throw new IllegalArgumentException("Empty String argument detected.");
        }

        int extensionIdx = filename.lastIndexOf(".");
        if (extensionIdx == -1) {
            logger.info("Received filename doesn't have extensions. filename : {}", filename);
            throw new UnsupportedImageFormatException("File extendsions does not exist.", IMAGE_FIELD, filename,
                    Collections.unmodifiableList(supportedExtensions));
        }

        String extension = filename.substring(extensionIdx, filename.length());

        logger.debug("Received file extension : {}", extension);

        supportedExtensions.stream()
                           .filter(supported -> supported.equals(extension))
                           .findAny()
                           .orElseThrow(() -> {
                               
                               logger.info("Received unsupported file extension. filename : {}", filename);
            
            throw new UnsupportedImageFormatException("Unsupported file extension. extension : " + extension,
                                   IMAGE_FIELD, filename, Collections.unmodifiableList(supportedExtensions));
        });

        logger.debug("Image file extension validation succeeded. ");
    }

}
