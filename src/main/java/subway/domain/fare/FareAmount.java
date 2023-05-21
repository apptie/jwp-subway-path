package subway.domain.fare;

import java.math.BigInteger;

public class FareAmount {

    private static final String INVALID_AMOUNT = "요금은 양수여야 합니다.";

    private final BigInteger amount;

    private FareAmount(final long amount) {
        validateAmount(amount);

        this.amount = BigInteger.valueOf(amount);
    }

    private FareAmount(final BigInteger amount) {
        validateAmount(amount);

        this.amount = amount;
    }

    private void validateAmount(final long amount) {
        if (amount < 0L) {
            throw new IllegalArgumentException(INVALID_AMOUNT);
        }
    }

    private void validateAmount(final BigInteger amount) {
        if (BigInteger.ZERO.compareTo(amount) > 0) {
            throw new IllegalArgumentException(INVALID_AMOUNT);
        }
    }

    public static FareAmount from(final long amount) {
        return new FareAmount(amount);
    }

    public int getAmount() {
        return amount.intValue();
    }

    public FareAmount plus(final FareAmount fareAmount) {
        return new FareAmount(this.amount.add(fareAmount.amount));
    }
}
