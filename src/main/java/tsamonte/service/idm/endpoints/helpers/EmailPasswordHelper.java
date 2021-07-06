package tsamonte.service.idm.endpoints.helpers;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import tsamonte.service.idm.database.model.UserModel;
import tsamonte.service.idm.models.request.LoginRegisterRequestModel;
import tsamonte.service.idm.security.Crypto;

public class EmailPasswordHelper {
    /**
        Returns true if email is of valid format <email>@<domain>.<extension>,
        where <email> is only alphanumeric
     */
    public static boolean isValidEmailFormat(String email) {
        String regex = "^[a-zA-Z0-9_]+@[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+$";
        if(email.matches(regex)) return true;
        return false;
    }

    /**
        Returns true if password is of length in range [7, 16]
     */
    public static boolean isValidPasswordLength(char[] password) {
        if (password.length >= 7 && password.length <= 16) return true;
        return false;
    }

    /**
        Returns true if password has valid contents:
            Must be at least 7 chars long
            Must contain at least one uppercase alpha
            Must contain at least one lowercase alpha
            Must contain at least one numeric
     */
    public static boolean isValidPasswordContents(char[] password) {
        String stringPassword = String.valueOf(password);
        String regex = "^.*(?=.{7,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).*$";
        if(stringPassword.matches(regex)) return true;
        return false;
    }

    public static boolean passwordsMatch(UserModel userResultFromDB, char[] givenPassword) {
        try {
            // decode salt stored in database
            byte[] salt = Hex.decodeHex(userResultFromDB.getSalt());

            // hash the given password with the salt retrieved from database
            byte[] hashedPassword = Crypto.hashPassword(givenPassword, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);

            String encodedGivenPassword = Hex.encodeHexString(hashedPassword);
            String hashedPasswordFromDB = userResultFromDB.getPword();

            // return comparison of both hashed/encoded passwords
            return encodedGivenPassword.equals(hashedPasswordFromDB);
        }
        catch (DecoderException e) {
            e.printStackTrace();
            return false;
        }
    }
}
