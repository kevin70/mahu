package cool.houge.mahu.admin;

import org.junit.jupiter.api.Test;
import org.roaringbitmap.buffer.MutableRoaringBitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/// @author ZY (kzou227@qq.com)
class RoaringBitmapTest {

    @Test
    void test1KIntSizeInBytes() {
        var o = new MutableRoaringBitmap();
        for (int i = 0; i < 1_000; i++) {
            var n = (int) (Math.random() * Integer.MAX_VALUE);
            o.add(n);
        }
        o.runOptimize();
        var sizeInBytes = o.serializedSizeInBytes();
        System.out.println(sizeInBytes);
    }

    @Test
    void test10KIntSizeInBytes() {
        var o = new MutableRoaringBitmap();
        for (int i = 0; i < 10_000; i++) {
            var n = (int) (Math.random() * Integer.MAX_VALUE);
            o.add(n);
        }
        o.runOptimize();
        var sizeInBytes = o.serializedSizeInBytes();
        System.out.println(sizeInBytes);
    }

    @Test
    void test100KIntSizeInBytes() {
        var o = new MutableRoaringBitmap();
        for (int i = 0; i < 100_000; i++) {
            var n = (int) (Math.random() * Integer.MAX_VALUE);
            o.add(n);
        }
        o.runOptimize();
        var sizeInBytes = o.serializedSizeInBytes();
        System.out.println(sizeInBytes);
    }

    @Test
    void test1000KIntSizeInBytes() throws IOException {
        var nums = new ArrayList<Integer>();
        var o = new MutableRoaringBitmap();
        for (int i = 0; i < 1000_000; i++) {
            var n = (int) (Math.random() * Integer.MAX_VALUE);
            o.add(n);
            nums.add(n);
        }
        o.runOptimize();

        var bos = new ByteArrayOutputStream();
        var dos = new DataOutputStream(bos);
        o.serialize(dos);
        dos.close();

        var bytes = bos.toByteArray();
        var rr2 = new MutableRoaringBitmap();
        rr2.deserialize(ByteBuffer.wrap(bytes));

        for (Integer n : nums) {
            assertThat(n).matches(rr2::contains);
        }
//        assertThat(0).matches(rr2::contains);
        System.out.println(bytes.length);
    }

}
