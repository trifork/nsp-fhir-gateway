package dk.medcom.edelivery.integration.dgws;

import dk.sosi.seal.model.IDCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SOSIContextIT {

    @Autowired
    SOSIContext sosiContext;

    @Test
    void id_card_is_valid_in_time() {
        IDCard idCard = sosiContext.getIdCard();
        assertTrue(idCard.isValidInTime());
    }
}
