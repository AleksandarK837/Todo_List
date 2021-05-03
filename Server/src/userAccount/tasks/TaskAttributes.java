package userAccount.tasks;

import java.io.Serializable;

public record TaskAttributes(String dueDate, String description) implements Serializable {
}
