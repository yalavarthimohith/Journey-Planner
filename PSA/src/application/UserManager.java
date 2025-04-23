package application;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
	private Map<String, User> users;
	private static final String USER_DATA_FILE = "registeredUsers.json";
	private Gson gson = new Gson();

	public UserManager() {
		this.users = new HashMap<>();
		loadUsersFromFile();
	}

	public boolean registerUser(String username, String email, String password) {
		if (users.containsKey(username) || isEmailRegistered(email)) {
			return false;
		}

		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

		User newUser = new User(username, email, hashedPassword);
		users.put(username, newUser);

		saveUsersToFile();
		return true;
	}

	private boolean isEmailRegistered(String email) {
		for (User user : users.values()) {
			if (user.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}

	public boolean authenticateUser(String username, String password) {
		User user = users.get(username);
		if (user != null) {
			System.out.println("Stored hashed password: " + user.getPassword());
			System.out.println("Entered password: " + password);

			if (BCrypt.checkpw(password, user.getPassword())) {
				return true;
			}
		}

		return false;
	}

	private void loadUsersFromFile() {
		try {
			File file = new File(USER_DATA_FILE);
			if (file.exists()) {

				FileReader reader = new FileReader(file);
				Type type = new TypeToken<Map<String, User>>() {
				}.getType();
				Map<String, User> loadedUsers = gson.fromJson(reader, type);
				if (loadedUsers != null) {
					users = loadedUsers;
				}
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveUsersToFile() {
		try {
			FileWriter writer = new FileWriter(USER_DATA_FILE);
			gson.toJson(users, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
