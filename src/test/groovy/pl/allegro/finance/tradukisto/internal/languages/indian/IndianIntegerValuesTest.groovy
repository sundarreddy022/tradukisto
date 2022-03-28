package pl.allegro.finance.tradukisto.internal.languages.indian

import pl.allegro.finance.tradukisto.internal.Container
import pl.allegro.finance.tradukisto.internal.converters.HundredsToWordsConverter
import spock.lang.Specification
import spock.lang.Unroll;

class IndianIntegerValuesTest extends Specification{

    def values = new IndianValues();
    def hundredsToWordsConverter =
            new HundredsToWordsConverter(values.baseNumbers(), values.twoDigitsNumberSeparator());
    def converter =
            new IndianIntegerToWordsConverter(hundredsToWordsConverter, values.pluralForms());

    @Unroll
    def "should convert #value to '#words' in Indian Words"(){
        expect:
        converter.asWords(value) == words

        where:
        value         | words
        0             | "zero"
        1             | "one"
        101           | "one hundred one"
        1001          | "one thousand one"
        10001         | "ten thousand one"
        100001        | "one lakh one"
        1000001       | "ten lakh one"
        10000001      | "one crore one"
        100000001     | "ten crore one"
        999999999     | "ninety-nine crore ninety-nine lakh ninety-nine thousand nine hundred ninety-nine"
    }
}
