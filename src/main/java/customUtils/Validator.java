package customUtils;

public class Validator {

    public static boolean validateDate(String date) {
        return (date != null && date.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

}
