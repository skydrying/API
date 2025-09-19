
package group.api.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFormattedTextField.AbstractFormatter;

public class TimeLabelFormatter extends AbstractFormatter {
    
    private String timePattern = "HH:mm"; // Формат времени
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(timePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return timeFormatter.parse(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) value);
            return timeFormatter.format(cal.getTime());
        }
        return "";
    }
}
