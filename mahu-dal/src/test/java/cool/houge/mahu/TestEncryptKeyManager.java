package cool.houge.mahu;

import io.ebean.config.EncryptKey;
import io.ebean.config.EncryptKeyManager;

public class TestEncryptKeyManager implements EncryptKeyManager {

    @Override
    public EncryptKey getEncryptKey(String tableName, String columnName) {
        return () ->
                "U2FsdGVkX19bWgik8nz4vH2LS/q0UIpgKFIMIWlESwYP5RS1SsKcSwRE4pLNoVPtZgMG4gsL1v97lxyBJHELPC+qAXrY/7pR1ePDhfy2G0s=";
    }
}
