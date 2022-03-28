package pl.allegro.finance.tradukisto.internal.languages.indian;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import pl.allegro.finance.tradukisto.internal.BigDecimalToStringConverter;
import pl.allegro.finance.tradukisto.internal.IntegerToStringConverter;
import pl.allegro.finance.tradukisto.internal.converters.HundredsToWordsConverter;

public class IndianBigDecimalToBankingMoneyConverter implements BigDecimalToStringConverter {

  private static final String FORMAT = "%s %s %02d/100";
  private static final int MAXIMAL_DECIMAL_PLACES_COUNT = 2;
  private static final BigDecimal BIG_DECIMAL_DIVISOR = new BigDecimal("10000000");

  private final IntegerToStringConverter converter;
  private final String currencySymbol;

  public IndianBigDecimalToBankingMoneyConverter(
      IntegerToStringConverter converter, String currencySymbol) {
    this.converter = converter;
    this.currencySymbol = currencySymbol;
  }

  @Override
  public String asWords(BigDecimal value) {

    int lengthOfBigDecimal = value.setScale(2, RoundingMode.HALF_UP).toString().length();
    StringBuilder stringBuilder = new StringBuilder();

    if (lengthOfBigDecimal > 10) {

      String[] croreInWords =
          asWords(value.divide(BIG_DECIMAL_DIVISOR, 0, RoundingMode.HALF_DOWN), currencySymbol)
              .split(" ");

      croreInWords = Arrays.copyOf(croreInWords, croreInWords.length - 2);
      Arrays.stream(croreInWords).sequential().forEach(s -> stringBuilder.append(s).append(" "));
      stringBuilder.append("crore ");
      stringBuilder.append(asWords(value.remainder(BIG_DECIMAL_DIVISOR), currencySymbol));

      return stringBuilder.toString();
    }

    return asWords(value, currencySymbol);
  }

  @Override
  public String asWords(BigDecimal value, String currencySymbol) {
    validate(value);

    Integer units = value.intValue();
    Integer subunits = value.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();

    return format(FORMAT, converter.asWords(units), currencySymbol, subunits);
  }

  private void validate(BigDecimal value) {
    checkArgument(
        value.scale() <= MAXIMAL_DECIMAL_PLACES_COUNT,
        "can't transform more than %s decimal places for value %s",
        MAXIMAL_DECIMAL_PLACES_COUNT,
        value);

    checkArgument(
        valueLessThatIntMax(value),
        "can't transform numbers greater than Integer.MAX_VALUE for value %s",
        value);

    checkArgument(
        valueGreaterThanOrEqualToZero(value),
        "can't transform negative numbers for value %s",
        value);
  }

  private boolean valueLessThatIntMax(BigDecimal value) {
    return value.compareTo(new BigDecimal(Integer.MAX_VALUE).add(BigDecimal.ONE)) == -1;
  }

  private boolean valueGreaterThanOrEqualToZero(BigDecimal value) {
    return value.signum() >= 0;
  }

}
