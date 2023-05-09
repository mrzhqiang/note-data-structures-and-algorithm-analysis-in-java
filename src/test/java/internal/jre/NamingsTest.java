package internal.jre;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class NamingsTest {

    @Test
    public void ofCamelToHyphen() {
        String expected = "abb-acc-add";
        String camel = Namings.ofCamelToHyphen("AbbAccAdd");
        log.debug("expected={}, camel={}", expected, camel);
        assertEquals(expected, camel);
    }

    @Test
    public void ofCanonical() {
        String expected = "internal_jre_namings-test.txt";
        String canonical = Namings.ofCanonical(getClass(), ".txt");
        log.debug("expected={}, canonical={}", expected, canonical);
        assertEquals(expected, canonical);
    }

    @Test
    public void ofSimple() {
        String expected = "namings-test.fxml";
        String simple = Namings.ofSimple(getClass(), ".fxml");
        log.debug("expected={}, simple={}", expected, simple);
        assertEquals(expected, simple);
    }

}