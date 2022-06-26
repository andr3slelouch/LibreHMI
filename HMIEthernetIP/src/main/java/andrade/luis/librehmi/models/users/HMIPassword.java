package andrade.luis.librehmi.models.users;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/*
    This Class was based on http://www.aspheute.com/english/20040105.asp special thanks to Christoph Wille
     and this response from StackOverFlow https://stackoverflow.com/a/18143616 special thanks to
    https://stackoverflow.com/users/829571/assylias
 */

public class HMIPassword {
    private static final Random RANDOM_GENERATOR = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;

    public static byte[] createRandomSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM_GENERATOR.nextBytes(saltBytes);
        return saltBytes;
    }

    public static String createRandomSaltString() {
        return Base64.getEncoder().encodeToString(createRandomSalt());
    }

    public static byte[] computeSaltedHash(char[] passwordToHash, byte[] saltToHash) throws IllegalArgumentException {
        try {
            PBEKeySpec spec = new PBEKeySpec(passwordToHash, saltToHash, ITERATIONS, KEY_LENGTH);
            Arrays.fill(passwordToHash, Character.MIN_VALUE);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return null;
        }
    }

    public static String computeSaltedHashString(String passwordToHash, String saltToHash) {
        return Base64.getEncoder().encodeToString(computeSaltedHash(passwordToHash.toCharArray(), Base64.getDecoder().decode(saltToHash)));
    }

    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        char[] passwordCharArr = password.toCharArray();
        byte[] saltedHash = computeSaltedHash(passwordCharArr, Base64.getDecoder().decode(salt));
        if(saltedHash!=null){
            Arrays.fill(passwordCharArr, Character.MIN_VALUE);
            String saltedHashStr = Base64.getEncoder().encodeToString(saltedHash);
            return saltedHashStr.equals(expectedHash);
        }else{
            return false;
        }

    }
}
