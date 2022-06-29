package tests.andrade.luis.librehmi.models.users;


import andrade.luis.librehmi.models.users.HMIPassword;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HMIPasswordTest {

    @Test
    public void testSaltGeneration(){
        String salt = HMIPassword.createRandomSaltString();
        String password = "12345";
        String computedHash = HMIPassword.computeSaltedHashString(password,salt);
        assertTrue(HMIPassword.verifyPassword(password,salt,computedHash));
    }
}
