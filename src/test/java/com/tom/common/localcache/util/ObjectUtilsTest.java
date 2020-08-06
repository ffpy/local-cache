package com.tom.common.localcache.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ObjectUtilsTest {

    @Test
    public void parseTest() {
        Assertions.assertThat(ObjectUtils.parse("1", Byte.class))
                .isEqualTo(Byte.valueOf("1"));
        Assertions.assertThat(ObjectUtils.parse("1", Short.class))
                .isEqualTo(Short.valueOf("1"));
        Assertions.assertThat(ObjectUtils.parse("1", Integer.class))
                .isEqualTo(Integer.valueOf("1"));
        Assertions.assertThat(ObjectUtils.parse("1", Long.class))
                .isEqualTo(Long.valueOf("1"));
        Assertions.assertThat(ObjectUtils.parse("1.1", Float.class))
                .isEqualTo(Float.valueOf("1.1"));
        Assertions.assertThat(ObjectUtils.parse("1.2", Double.class))
                .isEqualTo(Double.valueOf("1.2"));
        Assertions.assertThat(ObjectUtils.parse("true", Boolean.class))
                .isEqualTo(Boolean.valueOf("true"));
        Assertions.assertThat(ObjectUtils.parse("123", Character.class))
                .isEqualTo('1');
        Assertions.assertThat(ObjectUtils.parse("abc", String.class))
                .isEqualTo("abc");

        Assertions.assertThat(ObjectUtils.parse("1", byte.class))
                .isEqualTo(Byte.parseByte("1"));
        Assertions.assertThat(ObjectUtils.parse("1", short.class))
                .isEqualTo(Short.parseShort("1"));
        Assertions.assertThat(ObjectUtils.parse("1", int.class))
                .isEqualTo(Integer.parseInt("1"));
        Assertions.assertThat(ObjectUtils.parse("1", long.class))
                .isEqualTo(Long.parseLong("1"));
        Assertions.assertThat(ObjectUtils.parse("1.1", float.class))
                .isEqualTo(Float.parseFloat("1.1"));
        Assertions.assertThat(ObjectUtils.parse("1.2", double.class))
                .isEqualTo(Double.parseDouble("1.2"));
        Assertions.assertThat(ObjectUtils.parse("true", boolean.class))
                .isEqualTo(true);

        Assertions.assertThat(ObjectUtils.parse(null, Byte.class))
                .isNull();
        Assertions.assertThat(ObjectUtils.parse("", Byte.class))
                .isNull();
        Assertions.assertThat(ObjectUtils.parse(null, String.class))
                .isNull();
        Assertions.assertThat(ObjectUtils.parse("", String.class))
                .isEqualTo("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseUnsupportedTypeTest() {
        ObjectUtils.parse("1", Optional.class);
    }
}