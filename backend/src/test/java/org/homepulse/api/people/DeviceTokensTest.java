package org.homepulse.api.people;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DeviceTokensTest {

    private final DeviceTokens deviceTokens = new DeviceTokens();

    @Test
    void twoNewTokensReturnDifferentValues() {
        String token1 = deviceTokens.newToken();
        String token2 = deviceTokens.newToken();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void hashOfTheSameTokenReturnsTheSameValue() {
        String token1 = deviceTokens.newToken();
        String hash1 =  deviceTokens.hash(token1);
        String hash2 = deviceTokens.hash(token1);

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hashOfAbcReturnsExpectedValue() {
        String hash = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";
        String token = "abc";
        assertThat(deviceTokens.hash(token)).isEqualTo(hash);
    }
}
