package ru.basecode.ide.rest.plugin;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author danblack
 */
public class RequestParserTest extends Assert{

    @Test
    public void shouldNotChangeTheUrl() {
        assertEquals("param", RequestParser.encode("param"));
        assertEquals("?param", RequestParser.encode("?param"));
        assertEquals("?param=", RequestParser.encode("?param="));
        assertEquals("http://www.site.com?param", RequestParser.encode("http://www.site.com?param"));
        assertEquals("http://www.site.com?param=", RequestParser.encode("http://www.site.com?param="));
        assertEquals("http://www.site.com?param=value", RequestParser.encode("http://www.site.com?param=value"));
        assertEquals("http://www.site.com?param=value", RequestParser.encode("http://www.site.com?param=value"));
        assertEquals("http://www.site.com?param=value&param2=value2", RequestParser.encode("http://www.site.com?param=value&param2=value2"));
    }

    @Test
    public void shouldTrimSpacesInParamName() {
        assertEquals("?param", RequestParser.encode("?param "));
        assertEquals("?param", RequestParser.encode("? param "));
        assertEquals("?param", RequestParser.encode("? param"));
        assertEquals("?param=", RequestParser.encode("?param ="));
        assertEquals("?param=", RequestParser.encode("? param ="));
        assertEquals("?param=", RequestParser.encode("? param="));
    }

    @Test
    public void shouldEncodeSpaces() {
        assertEquals("?param=1+2", RequestParser.encode("?param=1 2"));
        assertEquals("?p+aram=1+2", RequestParser.encode("?p aram=1 2"));
    }

    @Test
    public void shouldEncodeSymbols() {
        assertEquals("?param=%D1%8B%D1%91", RequestParser.encode("?param=ыё"));
    }

}