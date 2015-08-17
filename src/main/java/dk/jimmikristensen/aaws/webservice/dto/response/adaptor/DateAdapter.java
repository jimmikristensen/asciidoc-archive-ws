package dk.jimmikristensen.aaws.webservice.dto.response.adaptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public DateAdapter() {
        dateFormat.setTimeZone(TimeZone.getDefault());
        dateFormat.setLenient(false);
    }

    @Override
    public Date unmarshal(String v) throws ParseException{
        if (v != null) {
            return dateFormat.parse(v);
        }
        return null;
    }

    @Override
    public String marshal(Date v) throws ParseException {
        if (v != null) {
            return dateFormat.format(v);
        }
        return null;
    }
}
