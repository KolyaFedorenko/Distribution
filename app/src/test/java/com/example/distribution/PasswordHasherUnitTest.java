package com.example.distribution;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(JUnit4.class)
public class PasswordHasherUnitTest {

    private PasswordHasher passwordHasher;

    private String smallPassword = "password";
    private String mediumPassword = "password12345678";
    private String largePassword = "passwordPSSWRD873626288756272824";
    private String generatedHash;

    @Before
    public void setUp() throws Exception{
        passwordHasher = new PasswordHasher();
    }

    @Test
    public void checkGeneratedHashLengthSame() throws Exception{
        generatedHash = passwordHasher.generatePasswordHash(smallPassword);
        assertEquals(32+128, generatedHash.length());
        generatedHash = passwordHasher.generatePasswordHash(mediumPassword);
        assertEquals(32+128, generatedHash.length());
        generatedHash = passwordHasher.generatePasswordHash(largePassword);
        assertEquals(32+128, generatedHash.length());
    }

    @Test
    public void checkValidatePassword() throws Exception{
        generatedHash = passwordHasher.generatePasswordHash(mediumPassword);
        assertTrue(passwordHasher.validatePassword(mediumPassword, generatedHash));
        assertFalse(passwordHasher.validatePassword(mediumPassword+"1",generatedHash));
    }

    @Test
    public void checkThatHashIsAlwaysDifferent() throws Exception{
        String previousHash = passwordHasher.generatePasswordHash(smallPassword);
        String nextHash = passwordHasher.generatePasswordHash(smallPassword);
        String lastHash = passwordHasher.generatePasswordHash(smallPassword);

        assertNotEquals(previousHash, nextHash);
        assertNotEquals(nextHash, lastHash);
        assertNotEquals(previousHash, lastHash);
    }

    @After
    public void shutDown() throws Exception{
        passwordHasher = null;
    }
}
