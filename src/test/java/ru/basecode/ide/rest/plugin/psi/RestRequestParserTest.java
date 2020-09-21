package ru.basecode.ide.rest.plugin.psi;

import org.junit.Assert;
import org.junit.Test;
import ru.basecode.ide.rest.plugin.psi.RestRequestParser;

/**
 * @author danblack
 */
public class RestRequestParserTest extends Assert{

    @Test
    public void shouldNotChangeTheUrl() {
        assertEquals("param", RestRequestParser.encode("param"));
        assertEquals("?param", RestRequestParser.encode("?param"));
        assertEquals("?param=", RestRequestParser.encode("?param="));
        assertEquals("http://www.site.com?param", RestRequestParser.encode("http://www.site.com?param"));
        assertEquals("http://www.site.com?param=", RestRequestParser.encode("http://www.site.com?param="));
        assertEquals("http://www.site.com?param=value", RestRequestParser.encode("http://www.site.com?param=value"));
        assertEquals("http://www.site.com?param=value", RestRequestParser.encode("http://www.site.com?param=value"));
        assertEquals("http://www.site.com?param=value&param2=value2", RestRequestParser.encode("http://www.site.com?param=value&param2=value2"));
    }

    @Test
    public void shouldTrimSpacesInParamName() {
        assertEquals("?param", RestRequestParser.encode("?param "));
        assertEquals("?param", RestRequestParser.encode("? param "));
        assertEquals("?param", RestRequestParser.encode("? param"));
        assertEquals("?param=", RestRequestParser.encode("?param ="));
        assertEquals("?param=", RestRequestParser.encode("? param ="));
        assertEquals("?param=", RestRequestParser.encode("? param="));
    }

    @Test
    public void shouldEncodeSpaces() {
        assertEquals("?param=1+2", RestRequestParser.encode("?param=1 2"));
        assertEquals("?p+aram=1+2", RestRequestParser.encode("?p aram=1 2"));
    }

    @Test
    public void shouldEncodeSymbols() {
        assertEquals("?param=%D1%8B%D1%91", RestRequestParser.encode("?param=ыё"));
    }

}
