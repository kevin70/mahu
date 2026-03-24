package cool.houge.mahu.admin.sys.dto;

import cool.houge.mahu.entity.sys.AdminNotification;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// 我的通知轮询结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeNotificationPollResult {

    /// 下一次轮询游标
    private String nextCursor;

    /// 是否还有更多数据
    private boolean hasMore;

    /// 当前未读总数
    private int unreadCount;

    /// 当前批次通知列表
    private List<AdminNotification> items;
}
