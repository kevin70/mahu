package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// HX801 设备日志
///
/// @author ZY (kzou227@qq.com)
@Entity
@Getter
@Setter
public class Hx801Log {

    /// 主键
    @Id
    private String id;
    /// 创建时间
    @WhenCreated
    private Instant createTime;
    /// 设备 UDI
    private String deviceId;
    private String bpb;
    private String fpb;
    private String dpb;
    private String rpb;
    private String pacc;
    private String pv;
    private String pa;
    private String p1st;
    private String temperature;
    private String feiyeLiang;
    private String touxiyeLiang;
    private String zhihuanyeLiang;
    private String tuoshuiLiang;
    private String hourmin;
    private String zongfeiyeLiang;
    private String zongtouxiyeLiang;
    private String zongzhihuanyeLiang;
    private String treatmentSeconds;
    private String treatmentDate;
}
