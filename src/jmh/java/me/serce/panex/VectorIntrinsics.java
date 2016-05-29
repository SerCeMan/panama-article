package me.serce.panex;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

/**
 * @author serce
 * @since 18.05.16.
 */
public class VectorIntrinsics {

    private static final MethodHandle _mm256_loadu_si256 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_loadu_si256", MethodType.methodType(Long4.class, long.class /*esi*/),
            true,
            0xC5, 0xFE, 0x6F, 0x06 // vmovdqu ymm0, YMMWORD PTR [rsi]
    );
    public static Long4 _mm256_loadu_si256(long address) throws Throwable {
        return (Long4) _mm256_loadu_si256.invokeExact(address);
    }

    private static final MethodHandle _mm256_storeu_si256 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_storeu_si256", MethodType.methodType(int.class, Long4.class, long.class),
            true,
            0xC5, 0xFE, 0x7F, 0x06 // vmovdqu ymm0, YMMWORD PTR [rsi]
    );
    public static int _mm256_storeu_si256(long address, Long4 l) throws Throwable {
        return (int) _mm256_storeu_si256.invokeExact(l, address);
    }


    private static final MethodHandle _mm256_unpacklo_epi8 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_loadu_si256", MethodType.methodType(Long4.class, Long4.class /*esi*/, Long4.class /*edx*/),
            true,
            0xC5, 0xFD, 0x60, 0xC1 // vpunpcklbw ymm0,ymm0,ymm1
    );
    public static Long4 _mm256_unpacklo_epi8(Long4 l1, Long4 l2) throws Throwable {
        return (Long4) _mm256_unpacklo_epi8.invokeExact(l1, l2);
    }

    private static final MethodHandle _mm256_unpackhi_epi8 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_unpackhi_epi8", MethodType.methodType(Long4.class, Long4.class /*esi*/, Long4.class /*edx*/),
            true,
            0xC5, 0xFD, 0x68, 0xC1 // vpunpckhbw ymm0,ymm0,ymm1
    );
    public static Long4 _mm256_unpackhi_epi8(Long4 l1, Long4 l2) throws Throwable {
        return (Long4) _mm256_unpackhi_epi8.invokeExact(l1, l2);
    }


    private static final MethodHandle _mm256_add_epi32 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_add_epi32", MethodType.methodType(Long4.class, Long4.class /*esi*/, Long4.class /*edx*/),
            true,
            0xC5, 0xF5, 0xFE, 0xC0 // vpaddd ymm0,ymm1,ymm0
    );
    public static Long4 _mm256_add_epi32(Long4 l1, Long4 l2) throws Throwable {
        return (Long4) _mm256_add_epi32.invokeExact(l1, l2);
    }

    private static final MethodHandle _mm256_madd_epi16 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_madd_epi16", MethodType.methodType(Long4.class, Long4.class /*esi*/, Long4.class /*edx*/),
            true,
            0xC5, 0xFD, 0xF5, 0xC1 // vpmaddwd ymm0,ymm0,ymm1
    );
    public static Long4 _mm256_madd_epi16(Long4 l1, Long4 l2) throws Throwable {
        return (Long4) _mm256_madd_epi16.invokeExact(l1, l2);
    }

    private static final MethodHandle _mm256_srli_si256_4 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_srli_si256_4", MethodType.methodType(Long4.class, Long4.class /*esi*/),
            true,
            0xC5, 0xFD, 0x73, 0xD8, 0x04 // vpsrldq ymm0,ymm0,0x4
    );
    public static Long4 _mm256_srli_si256_4(Long4 l) throws Throwable {
        return (Long4) _mm256_srli_si256_4.invokeExact(l);
    }

    private static final MethodHandle _mm256_srli_si256_8 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_srli_si256_8", MethodType.methodType(Long4.class, Long4.class /*esi*/),
            true,
            0xC5, 0xFD, 0x73, 0xD8, 0x08 // vpsrldq ymm0,ymm0,0x8
    );
    public static Long4 _mm256_srli_si256_8(Long4 l) throws Throwable {
        return (Long4) _mm256_srli_si256_8.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi32_0 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi32_0", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE3, 0x79, 0x16, 0xC0, 0x00, // vpextrd eax,xmm0,0x0
            0x48, 0x98, // cdqe
            0xC5, 0xF8, 0x77 // vzeroupper
    );
    public static long _mm256_extract_epi32_0(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi32_0.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi32_4 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi32_4", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE3, 0x7D, 0x39, 0xC0, 0x01, // vextracti128 xmm0,ymm0,0x1
            0xC4, 0xE3, 0x79, 0x16, 0xC0, 0x00, // vpextrd eax,xmm0,0x0
            0x48, 0x98,   // cdqe
            0xC5, 0xF8, 0x77 // vzeroupper
    );
    public static long _mm256_extract_epi32_4(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi32_4.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi64_0 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi64_0", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE1, 0xF9, 0x7E, 0xC0, //  vmovq  rax,xmm0
            0xC5, 0xF8, 0x77 // vzeroupper
    );
    public static long _mm256_extract_epi64_0(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi64_0.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi64_1 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi64_1", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE3, 0xF9, 0x16, 0xC0, 0x01,
            0xC5, 0xF8, 0x77
    );
    public static long _mm256_extract_epi64_1(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi64_1.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi64_2 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi64_2", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE3, 0x7D, 0x19, 0xC0, 0x01, 0xC4, 0xE1, 0xF9, 0x7E, 0xC0, 0xC5, 0xF8, 0x77
    );
    public static long _mm256_extract_epi64_2(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi64_2.invokeExact(l);
    }

    private static final MethodHandle _mm256_extract_epi64_3 = jdk.internal.panama.CodeSnippet.make(
            "_mm256_extract_epi64_3", MethodType.methodType(long.class, Long4.class /*esi*/),
            true,
            0xC4, 0xE3, 0x7D, 0x19, 0xC0, 0x01, 0xC4, 0xE3, 0xF9, 0x16, 0xC0, 0x01, 0xC5, 0xF8, 0x77
    );
    public static long _mm256_extract_epi64_3(Long4 l) throws Throwable {
        return (long) _mm256_extract_epi64_3.invokeExact(l);
    }
}
