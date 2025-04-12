package xperience;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class PasswordListTest {

    @TempDir
    Path tempDir;

    @Test
    void testPasswordListCreation() throws IOException {
        // Create a temporary password file
        Path passwordFile = tempDir.resolve("test-passwords.txt");
        List<String> passwords = Arrays.asList("pass1", "pass2", "pass3");
        Files.write(passwordFile, passwords);

        // Create PasswordList from the file
        PasswordList passwordList = new PasswordList(passwordFile.toString());

        // Test that passwords are loaded correctly
        assertTrue(passwordList.use("pass1"));
        assertTrue(passwordList.use("pass2"));
        assertTrue(passwordList.use("pass3"));

        // After using all passwords, they should be removed from the list
        assertFalse(passwordList.use("pass1"));
        assertFalse(passwordList.use("pass2"));
        assertFalse(passwordList.use("pass3"));
    }

    @Test
    void testInvalidPasswordFile() {
        Path nonExistentFile = tempDir.resolve("non-existent-file.txt");
        assertThrows(IOException.class, () -> new PasswordList(nonExistentFile.toString()));
    }

    @Test
    void testEmptyPasswordFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty-file.txt");
        Files.createFile(emptyFile);

        PasswordList passwordList = new PasswordList(emptyFile.toString());
        assertFalse(passwordList.use("anypassword"));
    }

    @Test
    void testPasswordUsage() throws IOException {
        Path passwordFile = tempDir.resolve("usage-test.txt");
        List<String> passwords = Arrays.asList("password123", "securePass", "testPassword");
        Files.write(passwordFile, passwords);

        PasswordList passwordList = new PasswordList(passwordFile.toString());

        // Valid password should return true and be consumed
        assertTrue(passwordList.use("password123"));
        // Using the same password again should fail as it's consumed
        assertFalse(passwordList.use("password123"));

        assertFalse(passwordList.use("wrongPassword"));

        assertTrue(passwordList.use("securePass"));
        assertTrue(passwordList.use("testPassword"));

        assertFalse(passwordList.use("password123"));
        assertFalse(passwordList.use("securePass"));
        assertFalse(passwordList.use("testPassword"));
    }
}