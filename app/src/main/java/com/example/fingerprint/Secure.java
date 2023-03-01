package com.example.fingerprint;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;


public class Secure {
    Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();


    public String main(String password){

        String hash = encoder.encode(password);
        return hash;
    }
    public boolean verify(String pass1, String pass2) {
            return encoder.matches(pass1, pass2);
    }


}
   /* public String main(String password1) {
        // salt 32 bytes
        // Hash length 64 bytes
        Argon2 argon2 = Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id,
                16,
                32);

        char[] password = password1.toCharArray();
        String hash = argon2.hash(3, // Number of iterations
                64 * 1024, // 64mb
                1, // how many parallel threads to use
                password);
        return hash;

    }*/
