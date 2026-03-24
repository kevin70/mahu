package cool.houge.mahu.admin.oas.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MeNotificationReadBatchRequest {

    /**
     * 通知 ID 列表
     */
      @NotNull
 @Size(min=1,max=200)
    @com.fasterxml.jackson.annotation.JsonProperty("notification_ids")
    private List<Long> notificationIds;
}
