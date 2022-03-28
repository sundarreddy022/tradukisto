package pl.allegro.finance.tradukisto.internal.languages.indian;

import pl.allegro.finance.tradukisto.internal.languages.GenderType;
import pl.allegro.finance.tradukisto.internal.languages.PluralForms;

public class IndianPluralForms implements PluralForms {

  private final String form;

  public IndianPluralForms(String form) {
    this.form = form;
  }

  public String formFor(Integer integer) {
    return this.form;
  }

  public GenderType genderType() {
    return GenderType.NON_APPLICABLE;
  }
}
