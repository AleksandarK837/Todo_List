package userAccount.tasks;

import java.io.Serializable;

public record Task(String name, String date) implements Serializable {
}
