package pl.allegro.finance.tradukisto.internal.converters;

import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.Arrays;
import pl.allegro.finance.tradukisto.internal.BigDecimalToStringConverter;
import pl.allegro.finance.tradukisto.internal.IntegerToStringConverter;

import java.math.BigDecimal;
import pl.allegro.finance.tradukisto.internal.languages.indian.IndianIntegerToWordsConverter;
import pl.allegro.finance.tradukisto.internal.languages.indian.IndianValues;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

public class BigDecimalToBankingMoneyConverter implements BigDecimalToStringConverter {

    private static final String FORMAT = "%s %s %02d/100";
    private static final int MAXIMAL_DECIMAL_PLACES_COUNT = 2;
    private static final BigDecimal BIG_DECIMAL_DIVISOR = new BigDecimal("10000000");

    private final IntegerToStringConverter converter;
    private final String currencySymbol;

    public BigDecimalToBankingMoneyConverter(IntegerToStringConverter converter, String currencySymbol) {
        this.converter = converter;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public String asWords(BigDecimal value) {

      int lengthOfBigDecimal = value.setScale(2, RoundingMode.HALF_UP).toString().length();
      StringBuilder stringBuilder = new StringBuilder();

      if(lengthOfBigDecimal > 10){

        String[] croreInWords = asWords(value.divide(BIG_DECIMAL_DIVISOR, 0, RoundingMode.HALF_DOWN), currencySymbol).split(" ");

        croreInWords = Arrays.copyOf(croreInWords, croreInWords.length-2);
        Arrays.stream(croreInWords).sequential().forEach(s -> stringBuilder.append(s+" "));
        stringBuilder.append(" crore ");
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
        checkArgument(value.scale() <= MAXIMAL_DECIMAL_PLACES_COUNT,
                "can't transform more than %s decimal places for value %s", MAXIMAL_DECIMAL_PLACES_COUNT, value);

        checkArgument(valueLessThatIntMax(value),
                "can't transform numbers greater than Integer.MAX_VALUE for value %s", value);

        checkArgument(valueGreaterThanOrEqualToZero(value),
                "can't transform negative numbers for value %s", value);
    }

    private boolean valueLessThatIntMax(BigDecimal value) {
        return value.compareTo(new BigDecimal(Integer.MAX_VALUE).add(BigDecimal.ONE)) == -1;
    }

    private boolean valueGreaterThanOrEqualToZero(BigDecimal value) {
        return value.signum() >= 0;
    }

  public static void main(String[] args) {
    IndianValues values = new IndianValues();
    HundredsToWordsConverter hundredsToWordsConverter = new HundredsToWordsConverter(values.baseNumbers(), values.twoDigitsNumberSeparator());

    IndianIntegerToWordsConverter integerConverter = new IndianIntegerToWordsConverter(hundredsToWordsConverter, values.pluralForms());
    BigDecimalToBankingMoneyConverter converter = new BigDecimalToBankingMoneyConverter(integerConverter, "Rupees");

      BigDecimal decimal = new BigDecimal("12123451232167");

    System.out.println("Value in Words ====== "+converter.asWords(decimal));
//      BigDecimal[] dec = decimal.divideAndRemainder(new BigDecimal(10000000));

//    System.out.println(decimal.remainder(new BigDecimal(10000000)));
//    BigDecimal dec = decimal.divide(new BigDecimal(10000000), 0, RoundingMode.HALF_DOWN);
//      dec[0] = decimal.subtract(dec[1]);
//    Arrays.stream(dec).forEach(a -> System.out.println("Here "+a));
//       Arrays.stream(dec).forEach( a -> System.out.println(converter.asWords(a)));


//      System.out.println(converter.asWords()));

  }
}

