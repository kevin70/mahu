package cool.houge.mahu.admin.oas.vo;

import cool.houge.mahu.admin.oas.vo.MeNotificationResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.avaje.validation.constraints.*;


@lombok.Data
@io.avaje.validation.constraints.Valid
public class MeNotificationPollResponse {

    /**
     * 下一次增量轮询使用的游标
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("next_cursor")
    private String nextCursor;
    /**
     * 是否还有更多数据
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("has_more")
    private Boolean hasMore;
    /**
     * 当前未读总数
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("unread_count")
    private Integer unreadCount;
    /**
     * 增量通知列表
     */
      @NotNull

    @com.fasterxml.jackson.annotation.JsonProperty("items")
    private List<@Valid MeNotificationResponse> items;
}
