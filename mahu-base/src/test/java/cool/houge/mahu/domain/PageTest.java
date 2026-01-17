package cool.houge.mahu.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
class PageTest {

    @Test
    void deser() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var json = "{\"page\":1,\"per_page\":20,\"sort\":[\"-create_at\",\"nickname\"]}";
        var bean = objectMapper.readValue(json, Page.class);
        System.out.println(bean);
    }

    @Data
    public static class TestBean {

        @JsonUnwrapped
        private Page page;
    }
}
