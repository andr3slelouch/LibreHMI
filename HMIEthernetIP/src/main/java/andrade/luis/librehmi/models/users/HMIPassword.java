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

/**
 * Esta clase permite verificar una contraseña para un usuario de la base de datos
 */
public class HMIPassword {
    private static final Random RANDOM_GENERATOR = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 512;

    private HMIPassword(){
        throw new IllegalStateException("Password class");
    }

    /**
     * Permite generar un salt pseudoaleatorio
     * @return array de byte conteniendo el salt
     */
    public static byte[] createRandomSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM_GENERATOR.nextBytes(saltBytes);
        return saltBytes;
    }

    /**
     * Permite generar un string pseudoaleatorio basado en el array de byte
     * @return String que contiene el salt pseudoaleatorio
     */
    public static String createRandomSaltString() {
        return Base64.getEncoder().encodeToString(createRandomSalt());
    }

    /**
     * Este método permite computar el hash basado en el salt y la contraseña ingresada por el usuario
     * @param passwordToHash Contraseña ingresada por el usuario
     * @param saltToHash Salt
     * @return Array de byte con el hash computado
     * @throws IllegalArgumentException Si algún parámetro no cumple las necesidades del algoritmo se llamará a
     * esta excepción
     */
    public static byte[] computeSaltedHash(char[] passwordToHash, byte[] saltToHash) throws IllegalArgumentException {
        try {
            PBEKeySpec spec = new PBEKeySpec(passwordToHash, saltToHash, ITERATIONS, KEY_LENGTH);
            Arrays.fill(passwordToHash, Character.MIN_VALUE);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return new byte[0];
        }
    }

    /**
     * Este método permite generar un string basado en un hash computado
     * @param passwordToHash Contraseña ingresada por el usuario
     * @param saltToHash Salt
     * @return String del hash computado
     */
    public static String computeSaltedHashString(String passwordToHash, String saltToHash) {
        return Base64.getEncoder().encodeToString(computeSaltedHash(passwordToHash.toCharArray(), Base64.getDecoder().decode(saltToHash)));
    }

    /**
     * Método verificador de contraseña, a partir del salt y su hash computado se verificará contra un nuevo cálculo de
     * hash con la contraseña ingresada por el usuario
     * @param password Contraseña ingresada por el usuario
     * @param salt Salt
     * @param expectedHash Hash computado
     * @return true: Si las contraseñas coinciden, caso contrario false
     */
    public static boolean verifyPassword(String password, String salt, String expectedHash) {
        char[] passwordCharArr = password.toCharArray();
        byte[] saltedHash = computeSaltedHash(passwordCharArr, Base64.getDecoder().decode(salt));
        if(saltedHash.length>0){
            Arrays.fill(passwordCharArr, Character.MIN_VALUE);
            String saltedHashStr = Base64.getEncoder().encodeToString(saltedHash);
            return saltedHashStr.equals(expectedHash);
        }else{
            return false;
        }
    }
}
