package com.android_aes.administrator.androidaes;

public class InsecureSHA1PRNGKeyDerivator {
    public static byte[] deriveInsecureKey(byte[] seed, int keySizeInBytes) {
        InsecureSHA1PRNGKeyDerivator derivator = new InsecureSHA1PRNGKeyDerivator();
        derivator.setSeed(seed);
        byte[] key = new byte[keySizeInBytes];
        derivator.nextBytes(key);
        return key;
    }
    private static final int[] END_FLAGS = { 0x80000000, 0x800000, 0x8000, 0x80 };
    private static final int[] RIGHT1 = { 0, 40, 48, 56 };
    private static final int[] RIGHT2 = { 0, 8, 16, 24 };
    private static final int[] LEFT = { 0, 24, 16, 8 };
    private static final int[] MASK = { 0xFFFFFFFF, 0x00FFFFFF, 0x0000FFFF,
            0x000000FF };
    private static final int HASHBYTES_TO_USE = 20;
    private static final int FRAME_LENGTH = 16;
    private static final int COUNTER_BASE = 0;
    private static final int HASHCOPY_OFFSET = 0;
    private static final int EXTRAFRAME_OFFSET = 5;
    private static final int FRAME_OFFSET = 21;
    private static final int MAX_BYTES = 48;
    private static final int UNDEFINED = 0;
    private static final int SET_SEED = 1;
    private static final int NEXT_BYTES = 2;

    private transient int[] seed;
    private transient long seedLength;
    private transient int[] copies;
    private transient byte[] nextBytes;
    private transient int nextBIndex;
    private transient long counter;
    private transient int state;
    private static final int H0 = 0x67452301;
    private static final int H1 = 0xEFCDAB89;
    private static final int H2 = 0x98BADCFE;
    private static final int H3 = 0x10325476;
    private static final int H4 = 0xC3D2E1F0;
    private static final int BYTES_OFFSET = 81;
    private static final int HASH_OFFSET = 82;
    private static final int DIGEST_LENGTH = 20;
    private InsecureSHA1PRNGKeyDerivator() {
        seed = new int[HASH_OFFSET + EXTRAFRAME_OFFSET];
        seed[HASH_OFFSET] = H0;
        seed[HASH_OFFSET + 1] = H1;
        seed[HASH_OFFSET + 2] = H2;
        seed[HASH_OFFSET + 3] = H3;
        seed[HASH_OFFSET + 4] = H4;
        seedLength = 0;
        copies = new int[2 * FRAME_LENGTH + EXTRAFRAME_OFFSET];
        nextBytes = new byte[DIGEST_LENGTH];
        nextBIndex = HASHBYTES_TO_USE;
        counter = COUNTER_BASE;
        state = UNDEFINED;
    }

    private void updateSeed(byte[] bytes) {
        updateHash(seed, bytes, 0, bytes.length - 1);
        seedLength += bytes.length;
    }

    private void setSeed(byte[] seed) {
        if (seed == null) {
            throw new NullPointerException("seed == null");
        }
        if (state == NEXT_BYTES) {
            System.arraycopy(copies, HASHCOPY_OFFSET, this.seed, HASH_OFFSET,
                    EXTRAFRAME_OFFSET);
        }
        state = SET_SEED;
        if (seed.length != 0) {
            updateSeed(seed);
        }
    }

    protected synchronized void nextBytes(byte[] bytes) {
        int i, n;
        long bits;
        int nextByteToReturn;
        int lastWord;
        final int extrabytes = 7;
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }
        lastWord = seed[BYTES_OFFSET] == 0 ? 0
                : (seed[BYTES_OFFSET] + extrabytes) >> 3 - 1;
        if (state == UNDEFINED) {
            throw new IllegalStateException("No seed supplied!");
        } else if (state == SET_SEED) {
            System.arraycopy(seed, HASH_OFFSET, copies, HASHCOPY_OFFSET,
                    EXTRAFRAME_OFFSET);

            for (i = lastWord + 3; i < FRAME_LENGTH + 2; i++) {
                seed[i] = 0;
            }
            bits = (seedLength << 3) + 64;

            if (seed[BYTES_OFFSET] < MAX_BYTES) {
                seed[14] = (int) (bits >>> 32);
                seed[15] = (int) (bits & 0xFFFFFFFF);
            } else {
                copies[EXTRAFRAME_OFFSET + 14] = (int) (bits >>> 32);
                copies[EXTRAFRAME_OFFSET + 15] = (int) (bits & 0xFFFFFFFF);
            }
            nextBIndex = HASHBYTES_TO_USE;
        }
        state = NEXT_BYTES;
        if (bytes.length == 0) {
            return;
        }
        nextByteToReturn = 0;
        n = (HASHBYTES_TO_USE - nextBIndex) < (bytes.length - nextByteToReturn) ? HASHBYTES_TO_USE
                - nextBIndex
                : bytes.length - nextByteToReturn;
        if (n > 0) {
            System.arraycopy(nextBytes, nextBIndex, bytes, nextByteToReturn, n);
            nextBIndex += n;
            nextByteToReturn += n;
        }
        if (nextByteToReturn >= bytes.length) {
            return; // return because "bytes[]" are filled in
        }
        n = seed[BYTES_OFFSET] & 0x03;
        for (;;) {
            if (n == 0) {
                seed[lastWord] = (int) (counter >>> 32);
                seed[lastWord + 1] = (int) (counter & 0xFFFFFFFF);
                seed[lastWord + 2] = END_FLAGS[0];
            } else {
                seed[lastWord] |= (int) ((counter >>> RIGHT1[n]) & MASK[n]);
                seed[lastWord + 1] = (int) ((counter >>> RIGHT2[n]) & 0xFFFFFFFF);
                seed[lastWord + 2] = (int) ((counter << LEFT[n]) | END_FLAGS[n]);
            }
            if (seed[BYTES_OFFSET] > MAX_BYTES) {
                copies[EXTRAFRAME_OFFSET] = seed[FRAME_LENGTH];
                copies[EXTRAFRAME_OFFSET + 1] = seed[FRAME_LENGTH + 1];
            }
            computeHash(seed);
            if (seed[BYTES_OFFSET] > MAX_BYTES) {
                System.arraycopy(seed, 0, copies, FRAME_OFFSET, FRAME_LENGTH);
                System.arraycopy(copies, EXTRAFRAME_OFFSET, seed, 0,
                        FRAME_LENGTH);
                computeHash(seed);
                System.arraycopy(copies, FRAME_OFFSET, seed, 0, FRAME_LENGTH);
            }
            counter++;
            int j = 0;
            for (i = 0; i < EXTRAFRAME_OFFSET; i++) {
                int k = seed[HASH_OFFSET + i];
                nextBytes[j] = (byte) (k >>> 24);
                nextBytes[j + 1] = (byte) (k >>> 16);
                nextBytes[j + 2] = (byte) (k >>> 8);
                nextBytes[j + 3] = (byte) (k);
                j += 4;
            }
            nextBIndex = 0;
            j = HASHBYTES_TO_USE < (bytes.length - nextByteToReturn) ? HASHBYTES_TO_USE
                    : bytes.length - nextByteToReturn;
            if (j > 0) {
                System.arraycopy(nextBytes, 0, bytes, nextByteToReturn, j);
                nextByteToReturn += j;
                nextBIndex += j;
            }
            if (nextByteToReturn >= bytes.length) {
                break;
            }
        }
    }

    private static void computeHash(int[] arrW) {
        int  a = arrW[HASH_OFFSET   ];
        int  b = arrW[HASH_OFFSET +1];
        int  c = arrW[HASH_OFFSET +2];
        int  d = arrW[HASH_OFFSET +3];
        int  e = arrW[HASH_OFFSET +4];
        int temp;
        for ( int t = 16; t < 80 ; t++ ) {
            temp  = arrW[t-3] ^ arrW[t-8] ^ arrW[t-14] ^ arrW[t-16];
            arrW[t] = ( temp<<1 ) | ( temp>>>31 );
        }
        for ( int t = 0 ; t < 20 ; t++ ) {
            temp = ( ( a<<5 ) | ( a>>>27 )   ) +
                    ( ( b & c) | ((~b) & d)   ) +
                    ( e + arrW[t] + 0x5A827999 ) ;
            e = d;
            d = c;
            c = ( b<<30 ) | ( b>>>2 ) ;
            b = a;
            a = temp;
        }
        for ( int t = 20 ; t < 40 ; t++ ) {
            temp = ((( a<<5 ) | ( a>>>27 ))) + (b ^ c ^ d) + (e + arrW[t] + 0x6ED9EBA1) ;
            e = d;
            d = c;
            c = ( b<<30 ) | ( b>>>2 ) ;
            b = a;
            a = temp;
        }
        for ( int t = 40 ; t < 60 ; t++ ) {
            temp = (( a<<5 ) | ( a>>>27 )) + ((b & c) | (b & d) | (c & d)) +
                    (e + arrW[t] + 0x8F1BBCDC) ;
            e = d;
            d = c;
            c = ( b<<30 ) | ( b>>>2 ) ;
            b = a;
            a = temp;
        }
        for ( int t = 60 ; t < 80 ; t++ ) {
            temp = ((( a<<5 ) | ( a>>>27 ))) + (b ^ c ^ d) + (e + arrW[t] + 0xCA62C1D6) ;
            e = d;
            d = c;
            c = ( b<<30 ) | ( b>>>2 ) ;
            b = a;
            a = temp;
        }
        arrW[HASH_OFFSET   ] += a;
        arrW[HASH_OFFSET +1] += b;
        arrW[HASH_OFFSET +2] += c;
        arrW[HASH_OFFSET +3] += d;
        arrW[HASH_OFFSET +4] += e;
    }

    private static void updateHash(int[] intArray, byte[] byteInput, int fromByte, int toByte) {
        int index = intArray[BYTES_OFFSET];
        int i = fromByte;
        int maxWord;
        int nBytes;
        int wordIndex = index >>2;
        int byteIndex = index & 0x03;
        intArray[BYTES_OFFSET] = ( index + toByte - fromByte + 1 ) & 077 ;

        if ( byteIndex != 0 ) {
            for ( ; ( i <= toByte ) && ( byteIndex < 4 ) ; i++ ) {
                intArray[wordIndex] |= ( byteInput[i] & 0xFF ) << ((3 - byteIndex)<<3) ;
                byteIndex++;
            }
            if ( byteIndex == 4 ) {
                wordIndex++;
                if ( wordIndex == 16 ) {
                    computeHash(intArray);
                    wordIndex = 0;
                }
            }
            if ( i > toByte ) {
                return ;
            }
        }
        maxWord = (toByte - i + 1) >> 2;
        for ( int k = 0; k < maxWord ; k++ ) {
            intArray[wordIndex] = ( ((int) byteInput[i   ] & 0xFF) <<24 ) |
                    ( ((int) byteInput[i +1] & 0xFF) <<16 ) |
                    ( ((int) byteInput[i +2] & 0xFF) <<8  ) |
                    ( ((int) byteInput[i +3] & 0xFF)      )  ;
            i += 4;
            wordIndex++;
            if ( wordIndex < 16 ) {
                continue;
            }
            computeHash(intArray);
            wordIndex = 0;
        }
        nBytes = toByte - i +1;
        if ( nBytes != 0 ) {
            int w =  ((int) byteInput[i] & 0xFF) <<24 ;
            if ( nBytes != 1 ) {
                w |= ((int) byteInput[i +1] & 0xFF) <<16 ;
                if ( nBytes != 2) {
                    w |= ((int) byteInput[i +2] & 0xFF) <<8 ;
                }
            }
            intArray[wordIndex] = w;
        }
    }
}