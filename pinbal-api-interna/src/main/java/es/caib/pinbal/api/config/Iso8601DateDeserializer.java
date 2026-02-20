package es.caib.pinbal.api.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Iso8601DateDeserializer extends JsonDeserializer<Date> {

    // Converteix +01:00 -> +0100 perquè SimpleDateFormat ho entengui (XXX no existeix a Java 7)
    private static final Pattern OFFSET_COLON = Pattern.compile("([+-]\\d{2}):(\\d{2})$");

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String s = p.getValueAsString();
        if (s == null || s.trim().isEmpty()) return null;

        s = s.trim();
        Matcher m = OFFSET_COLON.matcher(s);
        if (m.find()) {
            s = s.substring(0, m.start()) + m.group(1) + m.group(2);
        }

        // Intentem amb ms i sense ms
        Date d = tryParse(s, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        if (d != null) return d;

        d = tryParse(s, "yyyy-MM-dd'T'HH:mm:ssZ");
        if (d != null) return d;

        // Si ve "date-only" (no ideal, però compatible)
        d = tryParseAsUtc(s, "yyyy-MM-dd");
        if (d != null) return d;

        throw new IOException("Invalid ISO-8601 date: " + s);
    }

    private Date tryParse(String s, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ROOT);
            // IMPORTANT: el Z del text (offset) mana, no la timezone del sdf
            return sdf.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private Date tryParseAsUtc(String s, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ROOT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}
