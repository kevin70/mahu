package cool.houge.mahu.admin.service;

import com.github.f4b6a3.ulid.Ulid;
import cool.houge.mahu.admin.repository.Hx801LogRepository;
import cool.houge.mahu.entity.Hx801Log;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.regex.Pattern;

/// HX801 日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class Hx801Service {

    private final Pattern DEVICE_ID_PATTERN = Pattern.compile("^01(06976052848010)11(\\d{6})10(\\d{4,5})21(\\d{4})$");

    @Inject
    Hx801LogRepository hx801LogRepository;

    @Transactional
    public void save(Hx801Log entity) {
        if (!validateDeviceId(entity.getDeviceId())) {
            return;
        }

        entity.setId(Ulid.fast().toString());
        hx801LogRepository.save(entity);
    }

    public boolean validateDeviceId(String deviceId) {
        return DEVICE_ID_PATTERN.matcher(deviceId).matches();
    }
}
