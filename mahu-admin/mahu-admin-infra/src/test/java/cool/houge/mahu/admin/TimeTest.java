package cool.houge.mahu.admin;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

///
/// @author ZY (kzou227@qq.com)
public class TimeTest {

    @Test
    void execute() {
        System.out.println(Instant.now());

        var str = "2025-04-22T15:14:55";
        //        System.out.println(Instant.parse(str));

        var d = LocalDateTime.parse(str);
        System.out.println(d);
        System.out.println(d.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(d.atOffset(ZoneOffset.ofHours(8)).toInstant());

        System.out.println("==========");
        System.out.println(Instant.now().atZone(ZoneId.systemDefault()));
    }
}
