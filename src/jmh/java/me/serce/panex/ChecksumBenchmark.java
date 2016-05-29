package me.serce.panex;

import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static me.serce.panex.VectorIntrinsics.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class ChecksumBenchmark {
    static boolean isX64() {
        String arch = System.getProperties().getProperty("os.arch");
        return "x86_64".equals(arch) || "amd64".equals(arch);
    }

    static {
        // for JNI method
        System.loadLibrary("checksum");
    }

    static final MethodHandle sum3 = jdk.internal.panama.CodeSnippet.make(
            "sum3", MethodType.methodType(int.class, int.class /*rdi*/, int.class /*rsi*/, int.class /*rdx*/),
            true, /* isSupported */
            0x48, 0x89, 0xF0, // mov    rax,rsi
            0x48, 0x01, 0xF8, // add    rax,rdi
            0x48, 0x01, 0xD0  // add    rax,rdx
    );


    static final MethodHandle fastChecksum = jdk.internal.panama.CodeSnippet.make(
            "cpuid2", MethodType.methodType(int.class, long.class /*esi*/, int.class /*edx*/),
            isX64(),
//            0x55, 0x48, 0x89, 0xE5, 0x48, 0x83, 0xE4, 0xE0, 0x48, 0x83, 0xC4, 0x10, 0x66, 0xC7, 0x44, 0x24, 0xD0, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD2, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD4, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD6, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD8, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDA, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDC, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDE, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE0, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE2, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE4, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE6, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE8, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEA, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEC, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEE, 0x01, 0x00, 0xC5, 0xFD, 0x6F, 0x44, 0x24, 0xD0, 0x48, 0x83, 0xFE, 0x1F, 0x76, 0x41, 0x48, 0x8D, 0x56, 0xE0, 0xB8, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xE1, 0xEF, 0xDB, 0xC5, 0xD9, 0xEF, 0xE4, 0xC5, 0xFE, 0x6F, 0x0C, 0x07, 0xC5, 0xF5, 0x60, 0xD4, 0xC5, 0xF5, 0x68, 0xCC, 0xC5, 0xED, 0xF5, 0xD0, 0xC5, 0xE5, 0xFE, 0xDA, 0xC5, 0xF5, 0xF5, 0xC8, 0xC5, 0xE5, 0xFE, 0xD9, 0x48, 0x83, 0xC0, 0x20, 0x48, 0x39, 0xD0, 0x76, 0xDA, 0x48, 0x83, 0xE2, 0xE0, 0x48, 0x8D, 0x42, 0x20, 0xEB, 0x09, 0xB8, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xE1, 0xEF, 0xDB, 0x48, 0x39, 0xC6, 0x76, 0x1B, 0x48, 0x01, 0xF8, 0x48, 0x01, 0xFE, 0xBA, 0x00, 0x00, 0x00, 0x00, 0x0F, 0xBE, 0x08, 0x01, 0xCA, 0x48, 0x83, 0xC0, 0x01, 0x48, 0x39, 0xF0, 0x75, 0xF2, 0xEB, 0x05, 0xBA, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xFD, 0x73, 0xDB, 0x04, 0xC5, 0xE5, 0xFE, 0xD8, 0xC5, 0xFD, 0x73, 0xDB, 0x08, 0xC5, 0xE5, 0xFE, 0xD8, 0xC5, 0xF9, 0x6F, 0xC3, 0xC5, 0xF9, 0x7E, 0xC1, 0xC4, 0xE3, 0x7D, 0x39, 0xDB, 0x01, 0xC5, 0xF9, 0x7E, 0xD8, 0x01, 0xC8, 0x01, 0xC2, 0x89, 0xD1, 0xC1, 0xF9, 0x1F, 0xC1, 0xE9, 0x18, 0x01, 0xCA, 0x0F, 0xB6, 0xC2, 0x29, 0xC8, 0xC9
            0x55, 0xB8, 0x01, 0x00, 0x00, 0x00, 0xBA, 0x01, 0x00, 0x00, 0x00, 0xB9, 0x01, 0x00, 0x00, 0x00, 0x41, 0xB8, 0x01, 0x00, 0x00, 0x00, 0x41, 0xB9, 0x01, 0x00, 0x00, 0x00, 0x48, 0x89, 0xE5, 0x48, 0x83, 0xE4, 0xE0, 0x41, 0xBA, 0x01, 0x00, 0x00, 0x00, 0x48, 0x83, 0xC4, 0x10, 0x41, 0xBB, 0x01, 0x00, 0x00, 0x00, 0x48, 0x83, 0xFE, 0x1F, 0x66, 0x89, 0x44, 0x24, 0xD0, 0xB8, 0x01, 0x00, 0x00, 0x00, 0x66, 0x89, 0x54, 0x24, 0xD2, 0x66, 0x89, 0x44, 0x24, 0xDE, 0xB8, 0x01, 0x00, 0x00, 0x00, 0x66, 0x89, 0x4C, 0x24, 0xD4, 0x66, 0x89, 0x44, 0x24, 0xE0, 0xB8, 0x01, 0x00, 0x00, 0x00, 0x66, 0x44, 0x89, 0x44, 0x24, 0xD6, 0x66, 0x89, 0x44, 0x24, 0xE2, 0xB8, 0x01, 0x00, 0x00, 0x00, 0xBA, 0x01, 0x00, 0x00, 0x00, 0x66, 0x89, 0x44, 0x24, 0xE4, 0xB8, 0x01, 0x00, 0x00, 0x00, 0xB9, 0x01, 0x00, 0x00, 0x00, 0x66, 0x89, 0x44, 0x24, 0xE6, 0x41, 0xB8, 0x01, 0x00, 0x00, 0x00, 0xB8, 0x01, 0x00, 0x00, 0x00, 0x66, 0x44, 0x89, 0x4C, 0x24, 0xD8, 0x66, 0x44, 0x89, 0x54, 0x24, 0xDA, 0x66, 0x44, 0x89, 0x5C, 0x24, 0xDC, 0x66, 0x89, 0x44, 0x24, 0xE8, 0x66, 0x89, 0x54, 0x24, 0xEA, 0x66, 0x89, 0x4C, 0x24, 0xEC, 0x66, 0x44, 0x89, 0x44, 0x24, 0xEE, 0xC5, 0xFD, 0x6F, 0x44, 0x24, 0xD0, 0x76, 0x46, 0xC5, 0xE1, 0xEF, 0xDB, 0x48, 0x8D, 0x56, 0xE0, 0x31, 0xC0, 0xC5, 0xD9, 0xEF, 0xE4, 0xC5, 0xFE, 0x6F, 0x0C, 0x07, 0x48, 0x83, 0xC0, 0x20, 0x48, 0x39, 0xD0, 0xC5, 0xF5, 0x60, 0xD4, 0xC5, 0xF5, 0x68, 0xCC, 0xC5, 0xED, 0xF5, 0xD0, 0xC5, 0xE5, 0xFE, 0xDA, 0xC5, 0xF5, 0xF5, 0xC8, 0xC5, 0xE5, 0xFE, 0xD9, 0x76, 0xDA, 0x48, 0x83, 0xE2, 0xE0, 0xC5, 0xFD, 0x6F, 0xC3, 0x48, 0x83, 0xC2, 0x20, 0xEB, 0x0E, 0x0F, 0x1F, 0x40, 0x00, 0xC5, 0xF9, 0xEF, 0xC0, 0x31, 0xD2, 0xC5, 0xE1, 0xEF, 0xDB, 0x48, 0x39, 0xD6, 0x76, 0x4B, 0x48, 0x01, 0xFA, 0x48, 0x01, 0xFE, 0x31, 0xC0, 0x90, 0x0F, 0xBE, 0x0A, 0x48, 0x83, 0xC2, 0x01, 0x01, 0xC8, 0x48, 0x39, 0xF2, 0x75, 0xF2, 0xC5, 0xE5, 0x73, 0xDB, 0x04, 0xC5, 0xFD, 0xFE, 0xDB, 0xC5, 0xFD, 0x73, 0xDB, 0x08, 0xC5, 0xE5, 0xFE, 0xD8, 0xC5, 0xF9, 0x6F, 0xC3, 0xC4, 0xE3, 0x7D, 0x39, 0xDB, 0x01, 0xC4, 0xE3, 0x79, 0x16, 0xC1, 0x00, 0xC4, 0xE3, 0x79, 0x16, 0xDA, 0x00, 0x01, 0xCA, 0x01, 0xD0, 0x0F, 0xB6, 0xC0, 0xC5, 0xF8, 0x77, 0xEB, 0x04, 0x31, 0xC0, 0xEB, 0xC8, 0xC9
//            0x55, 0x48, 0x89, 0xE5, 0x48, 0x83, 0xE4, 0xE0, 0x48, 0x83, 0xC4, 0x10, 0x66, 0xC7, 0x44, 0x24, 0xD0, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD2, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD4, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD6, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xD8, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDA, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDC, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xDE, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE0, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE2, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE4, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE6, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xE8, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEA, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEC, 0x01, 0x00, 0x66, 0xC7, 0x44, 0x24, 0xEE, 0x01, 0x00, 0xC5, 0xFD, 0x6F, 0x44, 0x24, 0xD0, 0x48, 0x83, 0xFE, 0x1F, 0x76, 0x41, 0x48, 0x8D, 0x56, 0xE0, 0xB8, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xE1, 0xEF, 0xDB, 0xC5, 0xD9, 0xEF, 0xE4, 0xC5, 0xFE, 0x6F, 0x0C, 0x07, 0xC5, 0xF5, 0x60, 0xD4, 0xC5, 0xF5, 0x68, 0xCC, 0xC5, 0xED, 0xF5, 0xD0, 0xC5, 0xE5, 0xFE, 0xDA, 0xC5, 0xF5, 0xF5, 0xC8, 0xC5, 0xE5, 0xFE, 0xD9, 0x48, 0x83, 0xC0, 0x20, 0x48, 0x39, 0xD0, 0x76, 0xDA, 0x48, 0x83, 0xE2, 0xE0, 0x48, 0x8D, 0x52, 0x20, 0xEB, 0x09, 0xBA, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xE1, 0xEF, 0xDB, 0x48, 0x39, 0xD6, 0x76, 0x1B, 0x48, 0x01, 0xFA, 0x48, 0x01, 0xFE, 0xB8, 0x00, 0x00, 0x00, 0x00, 0x0F, 0xBE, 0x0A, 0x01, 0xC8, 0x48, 0x83, 0xC2, 0x01, 0x48, 0x39, 0xF2, 0x75, 0xF2, 0xEB, 0x05, 0xB8, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xFD, 0x73, 0xDB, 0x04, 0xC5, 0xE5, 0xFE, 0xD8, 0xC5, 0xFD, 0x73, 0xDB, 0x08, 0xC5, 0xE5, 0xFE, 0xD8, 0xC5, 0xF9, 0x6F, 0xC3, 0xC4, 0xE3, 0x79, 0x16, 0xC1, 0x00, 0xC4, 0xE3, 0x7D, 0x39, 0xDB, 0x01, 0xC4, 0xE3, 0x79, 0x16, 0xDA, 0x00, 0x01, 0xCA, 0x01, 0xD0, 0x0F, 0xB6, 0xC0, 0xC9
    );                               // pop rbx           ;; restore


    static final MethodHandle plainC_O2 = jdk.internal.panama.CodeSnippet.make(
            "cpuid2", MethodType.methodType(int.class, long.class, int.class),
            isX64(),
            0x48, 0x85, 0xF6, 0x74, 0x1E, 0x48, 0x01, 0xFE, 0x31, 0xC0, 0x66, 0x0F, 0x1F, 0x44, 0x00, 0x00, 0x0F, 0xBE, 0x17, 0x48, 0x83, 0xC7, 0x01, 0x01, 0xD0, 0x48, 0x39, 0xF7, 0x75, 0xF2, 0x0F, 0xB6, 0xC0, 0xEB, 0x02, 0x31, 0xC0);

    static final MethodHandle plainC_O3 = jdk.internal.panama.CodeSnippet.make(
            "cpuid2", MethodType.methodType(int.class, long.class /*esi*/, int.class /*edx*/),
            isX64(),
            0x55, 0x48, 0x89, 0xE5, 0x41, 0x56, 0x41, 0x55, 0x41, 0x54, 0x53, 0x48, 0x83, 0xE4, 0xE0, 0x48, 0x83, 0xC4, 0x10, 0x48, 0x85, 0xF6, 0x0F, 0x84, 0x54, 0x01, 0x00, 0x00, 0x48, 0x89, 0xF8, 0x49, 0x89, 0xF0, 0x48, 0xF7, 0xD8, 0x83, 0xE0, 0x1F, 0x48, 0x39, 0xF0, 0x48, 0x0F, 0x47, 0xC6, 0x48, 0x83, 0xFE, 0x26, 0x0F, 0x87, 0x17, 0x01, 0x00, 0x00, 0x31, 0xD2, 0x31, 0xC0, 0x0F, 0x1F, 0x00, 0x0F, 0xBE, 0x0C, 0x17, 0x48, 0x83, 0xC2, 0x01, 0x01, 0xC8, 0x4C, 0x39, 0xC2, 0x75, 0xF1, 0x48, 0x39, 0xD6, 0x0F, 0x84, 0xE7, 0x00, 0x00, 0x00, 0x49, 0x89, 0xF1, 0x4D, 0x29, 0xC1, 0x4D, 0x89, 0xCA, 0x49, 0xC1, 0xEA, 0x05, 0x4C, 0x89, 0xD1, 0x48, 0xC1, 0xE1, 0x05, 0x48, 0x85, 0xC9, 0x0F, 0x84, 0xBB, 0x00, 0x00, 0x00, 0xC5, 0xF9, 0xEF, 0xC0, 0x49, 0x01, 0xF8, 0x45, 0x31, 0xDB, 0xC4, 0xC1, 0x7D, 0x6F, 0x08, 0x49, 0x83, 0xC3, 0x01, 0x49, 0x83, 0xC0, 0x20, 0x4D, 0x39, 0xDA, 0xC4, 0xE2, 0x7D, 0x20, 0xD1, 0xC4, 0xE3, 0x7D, 0x39, 0xC9, 0x01, 0xC4, 0xE2, 0x7D, 0x23, 0xDA, 0xC4, 0xE3, 0x7D, 0x39, 0xD2, 0x01, 0xC4, 0xE2, 0x7D, 0x20, 0xC9, 0xC5, 0xE5, 0xFE, 0xC0, 0xC4, 0xE2, 0x7D, 0x23, 0xD2, 0xC5, 0xED, 0xFE, 0xC0, 0xC4, 0xE2, 0x7D, 0x23, 0xD1, 0xC4, 0xE3, 0x7D, 0x39, 0xC9, 0x01, 0xC5, 0xED, 0xFE, 0xC0, 0xC4, 0xE2, 0x7D, 0x23, 0xC9, 0xC5, 0xF5, 0xFE, 0xC0, 0x77, 0xAE, 0xC5, 0xF9, 0x6F, 0xC8, 0x48, 0x01, 0xCA, 0xC4, 0xE3, 0x7D, 0x39, 0xC0, 0x01, 0xC4, 0xC3, 0x79, 0x16, 0xCE, 0x00, 0xC4, 0xC3, 0x79, 0x16, 0xCD, 0x01, 0xC4, 0xC3, 0x79, 0x16, 0xCC, 0x02, 0xC4, 0xE3, 0x79, 0x16, 0xCB, 0x03, 0x45, 0x01, 0xF5, 0xC4, 0xC3, 0x79, 0x16, 0xC0, 0x00, 0x44, 0x01, 0xE8, 0xC4, 0xC3, 0x79, 0x16, 0xC3, 0x01, 0x44, 0x01, 0xE0, 0xC4, 0xC3, 0x79, 0x16, 0xC2, 0x02, 0x01, 0xD8, 0x41, 0x01, 0xC0, 0xC4, 0xE3, 0x79, 0x16, 0xC0, 0x03, 0x45, 0x01, 0xD8, 0x45, 0x01, 0xD0, 0x44, 0x01, 0xC0, 0x49, 0x39, 0xC9, 0x74, 0x3E, 0xC5, 0xF8, 0x77, 0x0F, 0x1F, 0x00, 0x0F, 0xBE, 0x0C, 0x17, 0x48, 0x83, 0xC2, 0x01, 0x01, 0xC8, 0x48, 0x39, 0xD6, 0x77, 0xF1, 0x0F, 0xB6, 0xC0, 0x48, 0x8D, 0x65, 0xE0, 0x5B, 0x41, 0x5C, 0x41, 0x5D, 0x41, 0x5E, 0x5D, 0xEB, 0x2C, 0x48, 0x85, 0xC0, 0x75, 0x1F, 0x45, 0x31, 0xC0, 0x31, 0xD2, 0x31, 0xC0, 0xE9, 0xF7, 0xFE, 0xFF, 0xFF, 0x0F, 0x1F, 0x80, 0x00, 0x00, 0x00, 0x00, 0xC5, 0xF8, 0x77, 0xEB, 0xD2, 0x0F, 0x1F, 0x00, 0x31, 0xC0, 0xEB, 0xCE, 0x49, 0x89, 0xC0, 0xE9, 0xBD, 0xFE, 0xFF, 0xFF);                               // pop rbx           ;; restore

    static final VarHandle VH = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN);


    public static long getAddress(ByteBuffer buffy) throws Throwable {
        Field address = Buffer.class.getDeclaredField("address");
        address.setAccessible(true);
        return address.getLong(buffy);
    }

    private ByteBuffer buffer;
    //    @Param({"4", "32", "128", "512", "2048", "8096", "32384", "129536"})
    @Param({
//            "4", "8096",
            "129536"})
    private int size = 4;
    private long address = 0;
    private ByteBuffer buf;
    private long tmpBuffAddr;

    private int a;
    private int b;
    private int c;

    @Setup
    public void setup() throws Throwable {
        buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < size / 4; i++) {
            buffer.putInt(random.nextInt());
        }
        address = getAddress(buffer);
        a = 5;
        b = 6;
        b = 7;

        buf = ByteBuffer.allocateDirect(256).order(ByteOrder.nativeOrder());
        for (int i = 0; i < 256 / 4; i++) {
            buf.putInt(0);
        }
        tmpBuffAddr = getAddress(buf);
    }

    private static int plainJavaChecksum(ByteBuffer buffer, int size) {
        int checksum = 0;
        for (int i = 0; i < size; ++i) {
            checksum += buffer.get(i);
        }
        return (int) (Integer.toUnsignedLong(checksum) % 256);
    }

    private static int varHandlesImpl(ByteBuffer buffer, int size) {
        int checksum = 0;
        int i;
        for (i = 0; i <= size - 8; i += 8) {
            long v = (long) VH.get(buffer, i);
            checksum += (v >> 56) +
                    (v >> 48) +
                    (v >> 40) +
                    (v >> 32) +
                    (v >> 24) +
                    (v >> 16) +
                    (v >> 8) +
                    v;
        }
        for (int j = i; j < size; j++) {
            checksum += buffer.get(j);
        }
        return (int) (Integer.toUnsignedLong(checksum) % 256);
    }

    public static native int nativePlainChecksum(long address, int size);

    private static final Long4 ones = Long4.make(
            0x0001000100010001L,
            0x0001000100010001L,
            0x0001000100010001L,
            0x0001000100010001L);
    private static final Long4 zero = Long4.make();
    public static final int aaa = 1;

    @CompilerControl(CompilerControl.Mode.INLINE)
    private static int JAVA_avxChecksumAVX2(ByteBuffer buffer, long target, int targetLength, long tmpBuffAddr) throws Throwable {
        final Long4 zeroVec = Long4.ZERO;
        final Long4 oneVec = ones;
        int checksum = 0;
        int offset = 0;

        if (targetLength >= 32) {
            for (; offset <= targetLength - 32; offset += 32) {
                Long4 vec = _mm256_loadu_si256(target + offset);
                Long4 accumIn = _mm256_loadu_si256(tmpBuffAddr);
                Long4 accum2 = _mm256_add_epi32(accumIn, _mm256_madd_epi16(_mm256_unpacklo_epi8(vec, zeroVec), oneVec));
                Long4 accum3 = _mm256_add_epi32(accum2, _mm256_madd_epi16(_mm256_unpackhi_epi8(vec, zeroVec), oneVec));
                _mm256_storeu_si256(tmpBuffAddr, accum3);
            }
        }
        Long4 accum = _mm256_loadu_si256(tmpBuffAddr);

        for (; offset < targetLength; ++offset) {
            checksum += (int) buffer.get(offset);
        }

        accum = _mm256_add_epi32(accum, _mm256_srli_si256_4(accum));
        accum = _mm256_add_epi32(accum, _mm256_srli_si256_8(accum));
        long checksum2 = (_mm256_extract_epi32_0(accum) + _mm256_extract_epi32_4(accum) + checksum);
        return (int) (Integer.toUnsignedLong((int) checksum2) % 256);
    }

//
    @Benchmark
    public int plainJava() {
        return plainJavaChecksum(buffer, size);
    }
//
//    @Benchmark
//    public int varHandlesJava() {
//        return varHandlesImpl(buffer, size);
//    }
//
//    @Benchmark
//    public int codeSnippetChecksum() throws Throwable {
//        return (int) plainC_O2.invoke(address, size);
//    }
//
//    @Benchmark
//    public int codeSnippetChecksumO3() throws Throwable {
//        return (int) plainC_O3.invoke(address, size);
//    }
//
//
//    @Benchmark
//    public int JNI_Checksum() throws Throwable {
//        return nativePlainChecksum(address, size);
//    }


//    @Benchmark
//    public long ea() throws Throwable {
//        return dd();
//    }
//
//    private static long dd() throws Throwable {
//        Long4 l = Long4.ZERO;
//        VectorIntrinsics._mm256_srli_si256_8(l);
//        return l.extract(0);
//    }
//
    @Benchmark
    public int avx2Impl() throws Throwable {
        return (int) fastChecksum.invoke(address, size);
    }

    @Benchmark
    public int JAVA_avx2Impl() throws Throwable {
        return JAVA_avxChecksumAVX2(buffer, address, size, tmpBuffAddr);
    }

//    @Benchmark
//    @CompilerControl(CompilerControl.Mode.PRINT)
//    public int sum2ben() throws Throwable {
//        return (int) sum2.invoke(a, b);
//    }

    public static int sum(int a, int b, int c) {
        return a + b + c;
    }

    public static native int sum_native(int a, int b, int c);


//    @Benchmark
//    public int benchSum3() throws Throwable {
//        return sum(a, b, c);
//    }


    public static void main(String[] args) throws Throwable {
        System.out.println();
//        ByteBuffer bb = ByteBuffer.allocate(8);
//        bb.putShort((short) 1);
//        bb.putShort((short) 1);
//        bb.putShort((short) 1);
//        bb.putShort((short) 1);
//        System.out.println(bb.position(0).getLong());

        if (!isX64()) return; // Not supported

        int size = 151587856;

        ByteBuffer buffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < size / 8; i++) {
            long value = random.nextLong();
            buffer.putLong(value);
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder());
        bb.putLong(0);bb.putLong(0);bb.putLong(0);bb.putLong(0);


        System.out.println(plainJavaChecksum(buffer, size));
        System.out.println(varHandlesImpl(buffer, size));
        System.out.println((int) plainC_O2.invoke(getAddress(buffer), size));
        System.out.println((int) plainC_O3.invoke(getAddress(buffer), size));
        System.out.println((int) fastChecksum.invoke(getAddress(buffer), size));
        System.out.println(nativePlainChecksum(getAddress(buffer), size));
        System.out.println(JAVA_avxChecksumAVX2(buffer, getAddress(buffer), size, getAddress(bb)));
    }
}
