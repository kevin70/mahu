package cool.houge.mahu.common;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/// 应用程序进程 ID
///
/// @author ZY (kzou227@qq.com)
public class PidFile {

    /// 将当前应用进程的 PID 写入指定
    ///
    /// @param filename PID 文件名称
    public static void writePidFile(String filename) {
        var pidFile = new File(filename);
        new PidFile().write(pidFile);
        pidFile.deleteOnExit();
    }

    private void write(File file) {
        var pid = ProcessHandle.current().pid();
        createParentDirectory(file);

        if (file.exists()) {
            try {
                assertCanOverwrite(file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.append(String.valueOf(pid));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    private void assertCanOverwrite(File file) throws IOException {
        if (!file.canWrite() || !canWritePosixFile(file)) {
            throw new FileNotFoundException(file + " (permission denied)");
        }
    }

    private boolean canWritePosixFile(File file) throws IOException {
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath());
            PosixFilePermission[] writePermissions = {
                PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE
            };
            for (PosixFilePermission permission : writePermissions) {
                if (permissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        } catch (UnsupportedOperationException ex) {
            // Assume that we can
            return true;
        }
    }
}
