package com.lifesup.gbtd.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateSerializer extends StdDeserializer<Date> {
    public JsonDateSerializer() {
        super(Date.class);
    }

    @SneakyThrows
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        String value = jsonParser.readValueAs(String.class);
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
