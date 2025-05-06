package cool.houge.mahu.entity;

import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/// 应用实例
///
/// @author ZY (kzou227@qq.com)
@Getter
@Setter
@Entity
@Table(name = "app_instance")
public class AppInstance {

    /// 主键
    @Id
    @GeneratedValue
    private Integer id;
    /// 创建时间
    @WhenCreated
    private Instant createdAt;
    /// 更新时间
    @WhenModified
    private Instant updatedAt;
    /// 应用名称
    private String appName;
    /// 应用版本
    private String appVersion;
    /// 进程 ID
    private long pid;
    /// 主机名称
    private String hostname;
    /// IP 公网地址
    private String ipAddr;
    /// 端口
    private int port;
    /// 工作目录
    private String workDir;
    /// Java 运行时环境版本
    private String javaVersion;
    /// Java 运行时环境供应商
    private String javaVendor;
    /// 操作系统版本
    private String osName;
    /// 操作系统版本
    private String osVersion;
    /// 操作系统架构
    private String osArch;
    /// 最后心跳时间
    private Instant lastHeartbeat;
}
