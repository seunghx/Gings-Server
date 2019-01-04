package com.gings.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;


@Component
@SuppressWarnings("rawtypes")
public class EnumConverterFactory implements ConverterFactory<String, Enum> {

    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            @SuppressWarnings("unchecked")
            T enumValue = (T) Enum.valueOf(this.enumType, source.trim());
            return enumValue;
        }
    }

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {

        @SuppressWarnings("unchecked")
        Converter<String, T> customConverter = new StringToEnumConverter(targetType);

        return customConverter;
    }

}


